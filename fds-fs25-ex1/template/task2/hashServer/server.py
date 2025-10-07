from concurrent import futures
import hashlib
import grpc

import hservice_pb2
import hservice_pb2_grpc
import dservice_pb2
import dservice_pb2_grpc


"""
Hash Server task:
-------------------
Given a one-time passcode issued by the Data Server, this service:
  1) fetches the user's text via GetAuthData(passcode),
  2) computes a SHA-256 digest of that text (UTF-8),
  3) returns the hex-encoded hash to the caller.

Exposes the `HS` service defined in hservice.proto.
"""


class HSImpl(hservice_pb2_grpc.HSServicer):
    """Concrete implementation of the `HS` service."""

    def GetHash(self, request, context):
        """
        RPC: GetHash(Request) -> Response

        Args:
            request: hservice_pb2.Request with fields:
                     - passcode: one-time capability issued by the Data Server
                     - ip:       Data Server host
                     - port:     Data Server port
            context: grpc.ServicerContext

        Returns:
            hservice_pb2.Response with field:
            - hash: hex string of SHA-256 over the fetched text.

        """
        # Validate required fields in the request
        if not request.passcode:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details("passcode is required")
            return hservice_pb2.Response()

        if not request.ip or request.port == 0:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details("ip and port are required")
            return hservice_pb2.Response()

        target = f"{request.ip}:{request.port}"
        print(f"Hash server: contacting Data Server at {target} with passcode {request.passcode}")

        # Call Data Server with the one-time passcode
        try:
            with grpc.insecure_channel(target) as ch:
                db = dservice_pb2_grpc.DBStub(ch)
                data = db.GetAuthData(
                    dservice_pb2.Passcode(code=request.passcode),
                    timeout=3.0,
                )
                print("Hash server: received data from Data Server:", repr(data))
        except grpc.RpcError as e:
            # Surface downstream status/description to the client
            context.set_code(e.code())
            context.set_details(f"Data server RPC failed: {e.details()}")
            return hservice_pb2.Response()

        # Compute SHA-256 over UTF-8 bytes; handle empty message safely
        msg = data.msg or ""
        digest = hashlib.sha256(msg.encode("utf-8")).hexdigest()

        return hservice_pb2.Response(hash=digest)


def serve():
    # Create the gRPC server, register the HS service, bind, and block.
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))

    # Register our HS implementation with this gRPC server:
    # - HSImpl() -> the servicer (implements GetHash)
    # - server   -> the grpc.Server instance to attach handlers to
    hservice_pb2_grpc.add_HSServicer_to_server(HSImpl(), server)

    # Bind on all interfaces, port 50052 (insecure per assignment).
    server.add_insecure_port("[::]:50052")

    server.start()
    print("Hash server listening on port 50052")
    server.wait_for_termination()


if __name__ == "__main__":
    serve()
