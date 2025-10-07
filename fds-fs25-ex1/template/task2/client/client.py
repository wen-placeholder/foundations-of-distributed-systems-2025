import sys
import grpc
import dservice_pb2
import dservice_pb2_grpc
import hservice_pb2
import hservice_pb2_grpc

"""
task:
1) Register with the data server (RegisterUser)
2) Store data on the data server (StoreData)
3) Generate a passcode on the data server (GenPasscode)
4) Ask the hash server to compute the hash with that passcode (GetHash)
"""

DATA_HOST, DATA_PORT = "127.0.0.1", 50051   # Data Server
HASH_HOST, HASH_PORT = "127.0.0.1", 50052   # Hash Server

# ---- Exit codes ----
EXIT_OK = 0
EXIT_STORE_FAILED = 1         # StoreData returned success=false (business failure)
EXIT_DATA_RPC_ERR = 2         # Any gRPC error while calling the Data Server
EXIT_HASH_RPC_ERR = 3         # Any gRPC error while calling the Hash Server


def main(username: str, password: str) -> int:
    text = "hello world from client"

    # --- 1) Data Server: register / store / generate passcode ---
    try:
        with grpc.insecure_channel(f"{DATA_HOST}:{DATA_PORT}") as ch:
            db = dservice_pb2_grpc.DBStub(ch)

            # Register user
            try:
                db.RegisterUser(
                    dservice_pb2.UserPass(username=username, password=password),
                    timeout=3.0,
                )
            except grpc.RpcError:
                pass

            # Store data (<1KB text)
            res = db.StoreData(
                dservice_pb2.StoreReq(username=username, password=password, msg=text),
                timeout=3.0,
            )
            if not getattr(res, "success", True):
                print("StoreData failed", file=sys.stderr)
                return EXIT_STORE_FAILED

            # Generate a one-time passcode
            pc = db.GenPasscode(
                dservice_pb2.UserPass(username=username, password=password),
                timeout=3.0,
            )
            passcode = pc.code
            print("Passcode:", passcode)
    except grpc.RpcError as e:
        print(f"Data server error: {e.code().name} - {e.details()}", file=sys.stderr)
        return EXIT_DATA_RPC_ERR

    # --- 2) Hash Server: compute hash with the passcode ---
    try:
        with grpc.insecure_channel(f"{HASH_HOST}:{HASH_PORT}") as ch2:
            hs = hservice_pb2_grpc.HSStub(ch2)
            resp = hs.GetHash(
                hservice_pb2.Request(passcode=passcode, ip=DATA_HOST, port=DATA_PORT),
                timeout=5.0,
            )
            print("SHA-256:", resp.hash)
    except grpc.RpcError as e:
        print(f"Hash server error: {e.code().name} - {e.details()}", file=sys.stderr)
        return EXIT_HASH_RPC_ERR

    return EXIT_OK


if __name__ == "__main__":
    # Usage: python client.py your_name.your_family_name password
    username = "your_team_name"
    password = "your_password"

    # Allow overriding via CLI: python client.py <username> <password>
    if len(sys.argv) >= 3:
        username, password = sys.argv[1], sys.argv[2]

    sys.exit(main(username, password))
