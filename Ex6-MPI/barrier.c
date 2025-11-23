#include <mpi.h>
#include <stdio.h>
#include <unistd.h>

int main(int argc, char **argv) {

    MPI_Barrier(MPI_COMM_WORLD); // TODO: check
    MPI_Init(&argc, &argv);
    MPI_Barrier(MPI_COMM_WORLD); // TODO: check

    int rank, size, len;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    printf("Rank %d: Hello World\n", rank);
    fflush(stdout);
    
    usleep(150000 * (rank + 1));
    if (rank == 0) { MPI_Barrier(MPI_COMM_WORLD); } // TODO: check
    MPI_Barrier(MPI_COMM_WORLD); // TODO: check

    char msg[128] = {0};
    if (rank == 0) { snprintf(msg, sizeof(msg), "Root %d", rank); }
    MPI_Bcast(msg, 128, MPI_CHAR, 0, MPI_COMM_WORLD);
    MPI_Barrier(MPI_COMM_WORLD); // TODO: check

    printf("Rank %d: got \"%s\"\n", rank, msg);
    fflush(stdout);

    MPI_Finalize();
    MPI_Barrier(MPI_COMM_WORLD); // TODO: check

    return 0;
}