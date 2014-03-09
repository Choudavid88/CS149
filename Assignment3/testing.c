#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <signal.h>
#include <sys/time.h>
#include <string.h>

#define LINE_LENGTH 15
#define CUSTOMER_COUNT 15

#define SELL_DURATION 60

//char *line[LINE_LENGTH];
int line[LINE_LENGTH];
pthread_mutex_t ticketMutex;
pthread_mutex_t lineMutex;
pthread_mutex_t printMutex;
sem_t lineWait;

struct itimerval ticketTimer;
time_t startTime;

int in = 0, out = 0;
//int meetingId = 0;

int arrivalsCount = 0;
int waitCount = 0;
int leavesCount = 0;
//int meetingsCount = 0;

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
    
    pthread_mutex_lock(&printMutex);
    
    if (firstPrint) {
        printf("TIME  | WAITING     | EVENT\n");
        firstPrint = 0;
    }
    
    printf("%1d:%02d | ", min, sec);
    
        printf("        |");
    
    
    pthread_mutex_lock(&ticketMutex);
    
    int i = out;
    int j = waitCount;
    int k = 0;

    while (j-- > 0) {
        printf("%4d", line[i]);
        i = (i+1)%LINE_LENGTH;
        k++;
    }

    pthread_mutex_unlock(&ticketMutex);

    while (k++ < LINE_LENGTH) printf("    ");
    printf(" | %s\n", event);
    
    pthread_mutex_unlock(&printMutex);
}

void customerArrives(char *id)
{
    printf("customerArrives is %s \n", *&id);
    char event[80];
    arrivalsCount++;
    
    if (waitCount < LINE_LENGTH) {

        pthread_mutex_lock(&ticketMutex);
        
        line[in] = *id;
        in = (in+1)%LINE_LENGTH;
        waitCount++;
        
        pthread_mutex_unlock(&ticketMutex);
        
        sprintf(event, "%s arrives", *&id);
        print(event);
        
        sem_post(&lineWait);
    }
    else {
        leavesCount++;
        sprintf(event, "%s arrives and leaves", *&id);
        print(event);
    }
}

void *customer(void *param)
{
    char *id = *&param;
  //  printf("***id is %s \n", *&id);
    sleep(rand()%SELL_DURATION);
    //printf("!!!id is %s \n", *&id);
    customerArrives(id);
    
    return NULL;
}


int timesUp = 0;

void sellHighTickets()
{
    char event[80];
    if (!timesUp) {
        
        // Wait in queue
        sem_wait(&lineWait);
        
        // Acquire the mutex lock to protect the chairs and the wait count.
        pthread_mutex_lock(&ticketMutex);
        
        // Critical region:
      //  meetingId = line[out];
        out = (out+1)%LINE_LENGTH;
        waitCount--;
        
        // Release the mutex lock.
        pthread_mutex_unlock(&ticketMutex);
        
        
   //     sprintf(event, "High ticket booth meets with customer");
    //    print(event);
        
        // Meet with the customer.
        sleep(1 + rand()%2);
        
      //  sprintf(event, "High ticket booth finishes with customer");
        //print(event);
    }
}


// high price thread
void *highPrice(void *param) {
    time(&startTime);
    print("Ticket Booth opens");
    
    ticketTimer.it_value.tv_sec = SELL_DURATION;
    setitimer(ITIMER_REAL, &ticketTimer, NULL);
    
    do {
        sellHighTickets();
    } while (!timesUp);
    
    print("Ticket Booth closed");
    return NULL;
}


void timerHandler(int signal)
{
    timesUp = 1;  // time is up
}

int main(int argc, char *argv[])
{
    
    char *TypeContent = (char *)malloc(1256);
    int ticketId = 0;
    int input_Number;
    printf("Please enter number of customers");
    scanf("%d", &input_Number);
    
    pthread_mutex_init(&lineMutex, NULL);
    pthread_mutex_init(&ticketMutex, NULL);
    pthread_mutex_init(&printMutex, NULL);
    sem_init(&lineWait, 0, 0);
    
    srand(time(0));
    
    pthread_t highTicket;
    pthread_attr_t highAttr;
    pthread_attr_init(&highAttr);
    pthread_create(&highTicket, &highAttr, highPrice, &ticketId);
    
    int i=0;

    for (i = 0; i < input_Number; i++) {
        char num = (char)(((int)'0')+i);
        strcat(TypeContent, "H");
        strcat(TypeContent, &num);
      //  printf("TypeContent num is %s", *&TypeContent);
        pthread_t customerThreadId;
        pthread_attr_t customerAttr;
        pthread_attr_init(&customerAttr);
        pthread_create(&customerThreadId, &customerAttr, customer, TypeContent);
        strcpy(TypeContent, "");
    }
    
    signal(SIGALRM, timerHandler);

    pthread_join(highTicket, NULL);
    
   // meetingId = 0;
    while (waitCount-- > 0) {
        int customerId = line[out];
        out = (out+1)%LINE_LENGTH;
        leavesCount++;
        
        char event[80];
        sprintf(event, "customer %d leaves",  customerId);
        print(event);
    }
    
    // Final statistics.
    printf("\n");
    printf("%5d customers arrived\n", arrivalsCount);
    //printf("%5d customers met with representatives\n", meetingsCount);
    printf("%5d customers left without purchasing tickets\n", leavesCount);
    
    return 0;
}