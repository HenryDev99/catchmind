#include <iostream>
#include <sys/socket.h>
#include <stdlib.h>
#include <stdio.h>
#include <string>
#include <unistd.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <netinet/in.h>
#include <random>

#include<map>
#include<algorithm>
#include<vector>

#define BUFSIZE    512
#define SERVERPORT "9000"
#define MAX_CLNT 100
#define MAX_IP 30
#define pp std::pair<int,int>

void * handle_clnt(void *arg);
void send_msg(char *msg, int len, bool a);
void error_handling(char* message);
char* serverState(int count);
void menu(char port[]);
int getRandom();
bool cmp(const pp& a, const pp& b);

int clnt_cnt = 0;
int clnt_socks[MAX_CLNT];
int temp = 0;

bool f_press = false, f_mainclnt = false;

pthread_mutex_t mutx;

std::string title[10] = {"악어","코끼리","상어","하마","고릴라","기린","가재","사자","호랑이", "오리너구리"};

std::string correct = "";
std::string good = "good/0/0";
std::string gameset = "set/0/0";
std::string haveHost = "no/0/0";
std::string gameend = "end/0/0";

int mainclnt, winclnt, gamecount = 0;

int nSockOpt;

std::random_device rd;
std::mt19937 gen(rd());

std::map<int, int> m;

int main() {
    int serv_sock, clnt_sock;
    
    struct sockaddr_in serv_addr;
    struct sockaddr_in clnt_addr;
    socklen_t clnt_addr_size;
    pthread_t t_id;
    
    menu(SERVERPORT);
    pthread_mutex_init(&mutx, NULL);
    serv_sock = socket(PF_INET, SOCK_STREAM, 0);
    if(serv_sock == -1)
        error_handling("socket error");
    
    memset(&serv_addr, 0, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr=htonl(INADDR_ANY);
    serv_addr.sin_port=htons(std::atoi(SERVERPORT));
    
    nSockOpt = 1;
    setsockopt(serv_sock, SOL_SOCKET, SO_REUSEADDR, &nSockOpt, sizeof(nSockOpt)); //bind 에러 해결법

    if(bind(serv_sock, (struct sockaddr*) &serv_addr, sizeof(serv_addr))==-1)
        error_handling("bind error");
    
    if(listen(serv_sock, 5)==-1)
        error_handling("listen error");
    
    
    while (1) {
        clnt_addr_size = sizeof(clnt_addr);
        clnt_sock=::accept(serv_sock, (struct sockaddr *)&clnt_addr, &clnt_addr_size);
        
        pthread_mutex_lock(&mutx);
        clnt_socks[clnt_cnt++]=clnt_sock;
        pthread_mutex_unlock(&mutx);
        
        pthread_create(&t_id, NULL, handle_clnt, (void*)&clnt_sock);
        pthread_detach(t_id);
        printf(" Connceted client IP : %s ", inet_ntoa(clnt_addr.sin_addr));
    }
    close(serv_sock);
    return 0;
}

void *handle_clnt(void *arg)
{
    int clnt_sock=*((int*)arg);
    int i, str_len;
    char msg[BUFSIZE];
    int titleValue = 0;

    memset(msg, 0, BUFSIZE);
    
    while((str_len=read(clnt_sock, msg, BUFSIZE))!=0)
    {
        printf("\n%s\n", msg);
        std::string a(msg);

        if(a == "host")
        {
            if(f_mainclnt)
            {
                memset(msg, 0, BUFSIZE);
                
                strncpy(msg, haveHost.c_str(), sizeof(msg));
                
                write(clnt_sock, msg, sizeof(msg)+1);
             
                memset(msg, 0, BUFSIZE);
            }
            else
            {
                f_mainclnt = true;
                
                titleValue = getRandom();
                
                correct = title[titleValue];
                
                temp = titleValue;
                
                strncpy(msg, correct.c_str(), sizeof(msg));
                
                write(clnt_sock, msg, sizeof(msg)+1);
             
                memset(msg, 0, BUFSIZE);

                for(i=0; i<clnt_cnt; i++)
                {
                    if(clnt_sock==clnt_socks[i])
                    {
                        mainclnt = i;
                        
                        printf("%d", mainclnt);
                    }
                }
            }
        }
        else if(a == "end")
        {
            std::vector<pp> vec( m.begin(), m.end() );
            sort(vec.begin(), vec.end(), cmp);

            for (auto num : vec) {
                std::cout << "key: "<< num.first << " | value: " << num.second << "\n";
            }
        }
        else
        {
            if(clnt_sock == clnt_socks[mainclnt])
            {
                send_msg(msg, sizeof(msg)+1, true);
                memset(msg, 0, BUFSIZE);
            }
            else
            {
                if(gamecount == 5)
                {
                    for (i=0; i<clnt_cnt; i++)
                    {
                        if(clnt_sock == clnt_socks[i])
                        {
                            std::cout<<i<<","<<atoi(msg);
                            m.insert(pp(i, atoi(msg)));
                        }
                    }
                    
                    memset(msg, 0, BUFSIZE);
                    strncpy(msg, gameend.c_str(), sizeof(msg));
                    write(clnt_socks[mainclnt], msg, sizeof(msg)+1);
                    memset(msg, 0, BUFSIZE);
                    
                }
                else
                {
                    if(correct==a)
                    {
                        memset(msg, 0, BUFSIZE);
                        strncpy(msg, good.c_str(), sizeof(msg));
                        write(clnt_sock, msg, sizeof(msg));
                        
                        ++gamecount;
                        if(gamecount == 5)
                        {
                            memset(msg, 0, BUFSIZE);
                            
                            strncpy(msg, gameend.c_str(), sizeof(msg));
                            
                            send_msg(msg, sizeof(msg), true);
                            
                            memset(msg, 0, BUFSIZE);
                        }
                        else
                        {
                            memset(msg, 0, BUFSIZE);
                            
                            titleValue = getRandom();
                            
                            std::cout<<titleValue<<temp;
                            
                            while(temp==titleValue)
                            {
                                titleValue = getRandom();
                                
                                std::cout<<"hi\n";
                            }
                            
                            temp = titleValue;
                            
                            correct = title[titleValue];
                            
                            strncpy(msg, correct.c_str(), sizeof(msg));
                            
                            write(clnt_socks[mainclnt], msg, sizeof(msg));
                            
                            memset(msg, 0, BUFSIZE);
                        }
                    }
                }
            }
        }
        a.clear();
        memset(msg, 0, BUFSIZE);
    }

    
    // remove disconnected client
    pthread_mutex_lock(&mutx);
    for (i=0; i<clnt_cnt; i++)
    {
        if (clnt_sock==clnt_socks[i])
        {
            while(i++<clnt_cnt-1)
                clnt_socks[i]=clnt_socks[i+1];
            break;
        }
    }
    if(clnt_sock == clnt_socks[mainclnt])
    {
        f_mainclnt = false;
        gamecount = 0;
    }
    clnt_cnt--;
    pthread_mutex_unlock(&mutx);
    close(clnt_sock);
    return NULL;
}

bool cmp(const pp& a, const pp& b) {
    if (a.second == b.second) return a.first < b.first;
    return a.second < b.second;
}

int getRandom()
{
    std::uniform_int_distribution<int> dis(0, 10);
    return dis(gen);
}
void send_msg(char* msg, int len, bool a)
{
    int i;
    pthread_mutex_lock(&mutx);
    if(a)
    {
        for (i=0; i<clnt_cnt; i++)
        {
            if(i != mainclnt)
                write(clnt_socks[i], msg, len);
        }
    }
    else
    {
        for (i=0; i<clnt_cnt; i++)
        {
            if(i == mainclnt)
                write(clnt_socks[i], msg, len);
        }
    }
    
        
    pthread_mutex_unlock(&mutx);
}
 
void error_handling(char *msg)
{
    fputs(msg, stderr);
    fputc('\n', stderr);
    exit(1);
}
 
char* serverState(int count)
{
    char* stateMsg = (char*)malloc(sizeof(char) * 20);
    strcpy(stateMsg ,"None");
    
    if (count < 5)
        strcpy(stateMsg, "Good");
    else
        strcpy(stateMsg, "Bad");
    
    return stateMsg;
}
 
void menu(char port[])
{
    system("clear");
    printf(" **** moon/sun chat server ****\n");
    printf(" server port    : %s\n", port);
    printf(" server state   : %s\n", serverState(clnt_cnt));
    printf(" max connection : %d\n", MAX_CLNT);
    printf(" ****          Log         ****\n\n");
}
