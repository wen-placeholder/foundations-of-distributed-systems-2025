#include <stdio.h>
#include <string.h>
#include <mpi.h>

#define MSG_TAG 0
#define ROOT_RANK 0

int main(int argc, char *argv[])
{
    int num_ranks, rankid, len;
    char hostname[MPI_MAX_PROCESSOR_NAME];
    char send_msg[256];
    char recv_msg[256];
    MPI_Request send_requests[256];  // Array to store send request handles
    MPI_Request recv_request;         // Request handle for receive
    MPI_Status status;

    MPI_Init(&argc, &argv);

    MPI_Comm_size(MPI_COMM_WORLD, &num_ranks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rankid);
    MPI_Get_processor_name(hostname, &len);

    if (rankid == ROOT_RANK) {
        // Root rank sends messages to all other ranks using non-blocking send
        snprintf(send_msg, sizeof(send_msg),
                 "Hello World from rank: %d on node: %s",
                 rankid, hostname);

        printf("Rank: %d on node: %s is sending messages (non-blocking)...\n",
               rankid, hostname);
        fflush(stdout);

        // Initiate non-blocking sends to all other ranks
        for (int i = 1; i < num_ranks; i++) {
            MPI_Isend(send_msg, strlen(send_msg) + 1, MPI_CHAR,
                      i, MSG_TAG, MPI_COMM_WORLD, &send_requests[i-1]);
        }

        printf("Rank: %d initiated all sends, now doing other work...\n", rankid);
        fflush(stdout);

        // Wait for all sends to complete
        MPI_Waitall(num_ranks - 1, send_requests, MPI_STATUSES_IGNORE);

        printf("Rank: %d finished sending to all ranks\n", rankid);
        fflush(stdout);
    }
    else {
        // Non-root ranks receive messages from root using non-blocking receive
        printf("Rank: %d on node: %s is ready to receive (non-blocking)...\n",
               rankid, hostname);
        fflush(stdout);

        // Initiate non-blocking receive
        MPI_Irecv(recv_msg, sizeof(recv_msg), MPI_CHAR,
                  ROOT_RANK, MSG_TAG, MPI_COMM_WORLD, &recv_request);

        printf("Rank: %d initiated receive, now doing other work...\n", rankid);
        fflush(stdout);

        // Wait for the receive to complete
        MPI_Wait(&recv_request, &status);

        printf("Rank: %d on node: %s received the following message: %s\n",
               rankid, hostname, recv_msg);
        fflush(stdout);
    }

    MPI_Finalize();
    return 0;
}
