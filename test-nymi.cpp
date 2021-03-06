#include "NymiSDK-v0.1.4/ncl.h"

#include <string>
#include <iostream>
#include <vector>

bool gNclInitialized = false;
std::vector<NclProvision> gProvisions;
int gHandle = -1;

struct {
    double HEADING;
    double PITCH;
    double LAT;
    double LONG;
} USERDATA;

USERDATA locData;

void callback(NclEvent event, void* userData)
{
    switch(event.type)
    {
        case NCL_EVENT_INIT:
            if(event.init.success) gNclInitialized = true;
            else exit(-1);
            break;
        case NCL_EVENT_DISCONNECTION:
            std::cout << "error: " << event.disconnection.reason;
        case NCL_EVENT_DISCOVERY:
            std::cout << "log: starting discovery\n";
            std::cout << "log: stopping scan\n";
            nclStopScan();
            gHandle = event.discovery.nymiHandle;
            std::cout << "log: agreeing\n";
            nclAgree(event.discovery.nymiHandle);
        case NCL_EVENT_AGREEMENT:
            gHandle = event.agreement.nymiHandle;
            std::cout << "Is this:\n";
            for (unsigned i = 0; i < NCL_AGREEMENT_PATTERNS; i++){
                for (unsigned j = 0; j < NCL_LEDS; j++)
                    std::cout << event.agreement.leds[i][j];
                std::cout << "\n";
            }
            std::cout << "The correct LED pattern (agree/reject)?\n";
            break;
        case NCL_EVENT_PROVISION:
            gProvisions.push_back(event.provision.provision);
            saveProvisionsToFile();
            std::cout << "log: provisioned\n";
            break;
        case NCL_EVENT_FIND:
            std::cout<<"log: Nymi found\n";
            std::cout<<"log: stopping scan\n";
            nclStopScan();
            gHandle=event.find.nymiHandle;
            std::cout<<"log: validating\n";
            nclValidate(event.find.nymiHandle);
            break;
        case NCL_EVENT_VALIDATION:
            std::cout<<"Nymi validated! Now trusted user stuff can happen.\n";
            
            break;
        default: break;
     }
}

int main(int argc, const char* argv[])
{
    locData.LONG = 10;
    locData.LAT = 0;
    locData.HEADING = 270;
    locData.PITCH = 0;

    if(!nclInit(callback, data, "HelloNymi", NCL_MODE_DEV, stderr)) return -1;
   
    getProvisionsFromFile();
    std::cout<<"Welcome to Hello Nymi!\n";
    std::cout<<"Enter \"provision\" if you want to start trusting a new Nymi.\n";
    std::cout<<"Enter \"validate\" if you want to find trusted Nymis and validate the first one found.\n";
    std::cout<<"Enter \"quit\" to quit.\n";

    //loop
      while(true){
        std::string input;
        std::cin>>input;
        if(!gNclInitialized){
            std::cout<<"error: NCL didn't finished initializing yet!\n";
            continue;
        }
        else if(input=="provision"){
            std::cout<<"log: starting discovery\n";
            nclStartDiscovery();
        }
        else if(input=="agree"){
            std::cout<<"log: provisioning\n";
            nclProvision(gHandle);
        }
        else if(input=="reject"){
            std::cout<<"log: disconnecting\n";
            nclDisconnect(gHandle);
        }
        else if(input=="validate"){
            std::cout<<"log: starting finding\n";
            nclStartFinding(gProvisions.data(), gProvisions.size(), NCL_FALSE);
        }
        else if(input=="disconnect"){
            std::cout<<"log: disconnecting\n";
            nclDisconnect(gHandle);
        }
        else if(input=="quit") break;
      }

      if (gHandle != -1) nclDisconnect(gHandle);
      nclFinish();
      return 0;
}

void saveProvisionsToFile(){
    ofstream file("provisions.txt");
    file<<gProvisions.size()<<"\n";
    for(unsigned i=0; i<gProvisions.size(); ++i){
        for(unsigned j=0; j<NCL_PROVISION_KEY_SIZE; ++j) file<<(int)gProvisions[i].key[j]<<" ";
        file<<" ";
        for(unsigned j=0; j<NCL_PROVISION_ID_SIZE; ++j) file<<(int)gProvisions[i].id[j]<<" ";
            file<<"\n";
    }
    file.close();
}

void getProvisionsFromFile(){
    ifstream file("provisions.txt");
    if(file.good()){
        cout<<"Loaded saved provision ID: ";
        unsigned size;
        file>>size;
        for(unsigned i=0; i<size; ++i){
            gProvisions.push_back(NclProvision());
            for(unsigned j=0; j<NCL_PROVISION_KEY_SIZE; ++j){
                unsigned b;
                file>>b;
                gProvisions.back().key[j]=b;
            }
            for(unsigned j=0; j<NCL_PROVISION_ID_SIZE; ++j){
                unsigned b;
                file>>b;
                gProvisions.back().id[j]=b;
                cout<<b;
            }
            cout<<"\n";
        }
    }
}
