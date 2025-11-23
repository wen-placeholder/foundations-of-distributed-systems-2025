#include <stdio.h>
#include <mpi.h>

int main(int argc, char *argv[])
{
    int num_ranks, rankid, len;
    char hostname[MPI_MAX_PROCESSOR_NAME];

    MPI_Init(&argc, &argv);

    MPI_Comm_size(MPI_COMM_WORLD, &num_ranks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rankid);
    MPI_Get_processor_name(hostname, &len);

    printf("Hello World from rank: %d on node: %s\n",
            rankid, hostname);
    fflush(stdout);

    MPI_Finalize();
    return 0;
}
