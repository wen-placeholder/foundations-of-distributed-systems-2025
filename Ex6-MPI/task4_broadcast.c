#include <stdio.h>
#include <string.h>
#include <mpi.h>

#define ROOT_RANK 0

int main(int argc, char *argv[])
{
    int num_ranks, rankid, len;
    char hostname[MPI_MAX_PROCESSOR_NAME];
    char message[256];

    MPI_Init(&argc, &argv);

    MPI_Comm_size(MPI_COMM_WORLD, &num_ranks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rankid);
    MPI_Get_processor_name(hostname, &len);

    if (rankid == ROOT_RANK) {
        // Root rank prepares the message to broadcast
        snprintf(message, sizeof(message),
                 "Hello World from rank: %d on node: %s",
                 rankid, hostname);

        printf("Rank: %d on node: %s is broadcasting message...\n",
               rankid, hostname);
        fflush(stdout);
    }

    // All ranks participate in the broadcast
    // Root sends, others receive
    MPI_Bcast(message, sizeof(message), MPI_CHAR, ROOT_RANK, MPI_COMM_WORLD);

    // Non-root ranks print the received message
    if (rankid != ROOT_RANK) {
        printf("Rank: %d on node: %s received the following message: %s\n",
               rankid, hostname, message);
        fflush(stdout);
    } else {
        printf("Rank: %d finished broadcasting to all ranks\n", rankid);
        fflush(stdout);
    }

    MPI_Finalize();
    return 0;
}
