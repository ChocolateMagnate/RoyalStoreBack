# Royal Store Backend
This repository contains the backend code for a side-project 
for demonstration purposes. It features email-and-password authentication,
multiple roles (guest, user, admin) and a set of REST APIs that will be useful
for a general-purpose Internet store. The service handles transactions in creation,
removing, updating and getting multiple categories of products, including smartphones
and laptops. It can be further extended and scaled to handle more products, authorization
providers and integrated with caching. It also is ready for deployment and includes the 
Dockerfile that can be installed on the server and invoked. 

The link to the [front-end side](https://github.com/staybacc/rooting/compare) can be found here.

To run the server locally, clone the repository: 
```commandline
git clone https://github.com/ChocolateMagnate/RoyalStoreBack
```
Navigate to the directory:
```commandline
cd RoyalStoreBack
```
Start the server:
```commandline
docker-compose up
```

Note: if you are on Windows, make sure the Docker service is running in 
Docker Desktop. For Linux users, you can check it from systemd:
```commandline
systemctl status docker
```
Make sure it is `Active: active (running)`. Sample output:
```text
● docker.service - Docker Application Container Engine
     Loaded: loaded (/usr/lib/systemd/system/docker.service; enabled; preset: disabled)
    Drop-In: /usr/lib/systemd/system/service.d
             └─10-timeout-abort.conf
     Active: active (running) since Tue 2024-01-09 17:39:09 EET; 3 days ago
TriggeredBy: ● docker.socket
       Docs: https://docs.docker.com
   Main PID: 1075 (dockerd)
      Tasks: 16
     Memory: 37.0M
        CPU: 33.805s
     CGroup: /system.slice/docker.service
             └─1075 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
```
If it's not running, start it with
```commandline
sudo systemctl start docker
```