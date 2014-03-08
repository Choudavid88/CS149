#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <signal.h>
#include <sys/time.h>
#include <string.h>

#define ID_BASE 101

#define LINE_LENGTH 15
#define STUDENT_COUNT 15

#define MAX_MEETING_DURATION 5
#define OFFICE_HOUR_DURATION 60

//char *line[LINE_LENGTH];     // circular buffer of chairs
int line[LINE_LENGTH];     // circular buffer of chairs
pthread_mutex_t ticketMutex;  // mutex protects chairs and wait count
pthread_mutex_t lineMutex;  // mutex protects chairs and wait count
pthread_mutex_t printMutex;  // mutex protects printing
sem_t lineWait;          // professor waits on this semaphore

struct itimerval ticketTimer;  // professor's office hour timer
time_t startTime;

int in = 0, out = 0;
int meetingId = 0;

int arrivalsCount = 0;
int waitCount = 0;
int leavesCount = 0;
int meetingsCount = 0;

int firstPrint = 1;

// Print a line for each event:
//   elapsed time
//   who is meeting with the professor
//   who is waiting in the chairs
//   what event occurred
void print(char *event)
{
    time_t now;
    time(&now);
    double elapsed = difftime(now, startTime);
    int min = 0;
    int sec = (int) elapsed;
    
    if (sec >= 60) {
        min++;
        sec -= 60;
    }
    
    // Acquire the mutex lock to protect the printing.
    pthread_mutex_lock(&printMutex);
    
    if (firstPrint) {
        printf("TIME | MEETING | WAITING     | EVENT\n");
        firstPrint = 0;
    }
    
    // Elapsed time.
    printf("%1d:%02d | ", min, sec);
    
    // Who's meeting with the ticket booth.
    if (meetingId > 0) {
        printf("%5d   |", meetingId);
    }
    else {
        printf("        |");
    }
    
    // Acquire the mutex lock to protect the chairs and the wait count.
    pthread_mutex_lock(&ticketMutex);
    
    int i = out;
    int j = waitCount;
    int k = 0;
    
    // Who's waiting in line.
    while (j-- > 0) {
   //     printf("%4d", *line[i]);
        printf("%4d", line[i]);
        i = (i+1)%LINE_LENGTH;
        k++;
    }
    
    // Release the mutex lock.
    pthread_mutex_unlock(&ticketMutex);
    
    // What event occurred.
    while (k++ < LINE_LENGTH) printf("    ");
    printf(" | %s\n", event);
    
    // Release the mutex lock.
    pthread_mutex_unlock(&printMutex);
}

// A student arrives.
void studentArrives(char *id)
{
    printf("studentArrives is %s \n", *&id);
    char event[80];
    arrivalsCount++;
    
    if (waitCount < LINE_LENGTH) {
        
        // Acquire the mutex lock to protect the chairs and the wait count.
        pthread_mutex_lock(&ticketMutex);
        
        // Seat a student into a chair.
     //   line[in] = id;
        line[in] = *id;
        in = (in+1)%LINE_LENGTH;
        waitCount++;
        
        // Release the mutex lock.
        pthread_mutex_unlock(&ticketMutex);
        
        sprintf(event, "%s arrives", *&id);
        print(event);
        
        // Signal the "filledSlots" semaphore.
        sem_post(&lineWait);  // signal
    }
    else {
        leavesCount++;
        sprintf(event, "%s arrives and leaves", *&id);
        print(event);
    }
}

void *customer(void *param)
{
  //  printf("param is %s \n", *&param);
    char *id = *&param;
    //int id = *((int *) param);
    printf("***id is %s \n", *&id);
    // Students will arrive at random times during the office hour.
    sleep(rand()%OFFICE_HOUR_DURATION);
    printf("!!!id is %s \n", *&id);
    studentArrives(id);
    
    return NULL;
}


int timesUp = 0;  // 1 = office hour is over

void sellHighTickets()
{
    char event[80];
    if (!timesUp) {
        if(meetingId == 0) {
            return;
        }
        // Wait on the "filledChairs" semaphore for a student.
        sem_wait(&lineWait); //filledSpots
        
        // Acquire the mutex lock to protect the chairs and the wait count.
        pthread_mutex_lock(&ticketMutex);
        
        // Critical region: Remove a student from a chair.
        meetingId = line[out];
        out = (out+1)%LINE_LENGTH;
        waitCount--;
        
        // Release the mutex lock.
        pthread_mutex_unlock(&ticketMutex);
        
        
   //     sprintf(event, "High ticket booth meets with customer %d",  meetingId);
    //    print(event);
        
        // Meet with the customer.
        sleep(1 + rand()%2);
        meetingsCount++;
        
      //  sprintf(event, "High ticket booth finishes with customer %d",  meetingId);
       // meetingId = 0;
        //print(event);
    }
}


// The professor thread.
void *highPrice(void *param) {
    time(&startTime);
    print("Ticket Booth opens");
    
    // Set the timer for for office hour duration.
    ticketTimer.it_value.tv_sec = OFFICE_HOUR_DURATION;
    setitimer(ITIMER_REAL, &ticketTimer, NULL);
    
    // sell tickets until the  hour is over.
    do {
        sellHighTickets();
    } while (!timesUp);
    
    print("Ticket Booth closed");
    return NULL;
}

// Timer signal handler.
void timerHandler(int signal)
{
    timesUp = 1;  // office hour is over
}

// Main.
int main(int argc, char *argv[])
{
    char *TypeContent = (char *)malloc(1256);
    //char *TypeContent = NULL;
  //  int studentIds[STUDENT_COUNT];
   // int professorId = 0;
    int ticketId = 0;
    // Initialize the mutexes and the semaphore.
    pthread_mutex_init(&lineMutex, NULL);
    pthread_mutex_init(&ticketMutex, NULL);
    pthread_mutex_init(&printMutex, NULL);
    sem_init(&lineWait, 0, 0);
    
    srand(time(0));
    
    // Create the professor thread.
    pthread_t highTicket;
    pthread_attr_t highAttr;
    pthread_attr_init(&highAttr);
    pthread_create(&highTicket, &highAttr, highPrice, &ticketId);
    
    // Create the student threads.
    int i=0;
  //  for (i = 0; i < STUDENT_COUNT; i++) {
    for (i = 0; i < 5; i++) {
        char num = (char)(((int)'0')+i);
        strcat(TypeContent, "H");
        strcat(TypeContent, &num);
        printf("TypeContent num is %s", *&TypeContent);
        pthread_t customerThreadId;
        pthread_attr_t customerAttr;
        pthread_attr_init(&customerAttr);
        pthread_create(&customerThreadId, &customerAttr, customer, TypeContent);
        strcpy(TypeContent, "");
    }
    
    // Set the timer signal handler.
    signal(SIGALRM, timerHandler);
    
    // Wait for the professor to complete the office hour.
    pthread_join(highTicket, NULL);
    
    // Remaining waiting students leave.
    meetingId = 0;
    while (waitCount-- > 0) {
        int customerId = line[out];
       // char *studentId = line[out];
        out = (out+1)%LINE_LENGTH;
        leavesCount++;
        
        char event[80];
        //sprintf(event, "Student %s leaves",  *&studentId);
        sprintf(event, "customer %d leaves",  customerId);
        print(event);
    }
    
    // Final statistics.
    printf("\n");
    printf("%5d customers arrived\n", arrivalsCount);
    printf("%5d customers met with representatives\n", meetingsCount);
    printf("%5d customers left without purchasing tickets\n", leavesCount);
    
    return 0;
}