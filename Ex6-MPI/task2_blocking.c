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
    MPI_Status status;

    MPI_Init(&argc, &argv);

    MPI_Comm_size(MPI_COMM_WORLD, &num_ranks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rankid);
    MPI_Get_processor_name(hostname, &len);

    if (rankid == ROOT_RANK) {
        // Root rank sends messages to all other ranks
        snprintf(send_msg, sizeof(send_msg),
                 "Hello World from rank: %d on node: %s",
                 rankid, hostname);

        printf("Rank: %d on node: %s is sending messages...\n",
               rankid, hostname);
        fflush(stdout);

        for (int i = 1; i < num_ranks; i++) {
            MPI_Send(send_msg, strlen(send_msg) + 1, MPI_CHAR,
                     i, MSG_TAG, MPI_COMM_WORLD);
        }

        printf("Rank: %d finished sending to all ranks\n", rankid);
        fflush(stdout);
    }
    else {
        // Non-root ranks receive messages from root
        MPI_Recv(recv_msg, sizeof(recv_msg), MPI_CHAR,
                 ROOT_RANK, MSG_TAG, MPI_COMM_WORLD, &status);

        printf("Rank: %d on node: %s received the following message: %s\n",
               rankid, hostname, recv_msg);
        fflush(stdout);
    }

    MPI_Finalize();
    return 0;
}
