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
cd RoyalStoreBack/RoyalStore
```
You will need to set the secrets that will hold sensitive 
information. Precisely, you will need to set the following variables:
1. `MONGODB_USERNAME`: sets the username MongoDB client
2. `MONGODB_PASSWORD`: sets the password needed to connect
3. `MONGODB_DATABASE`: sets the database name where artefacts will be stored
4. `MONGODB_URL`: the [URI string](https://www.mongodb.com/docs/manual/reference/connection-string/) needed to connect to the database
5. `JWT_SINGING_KEY`: the secret cryptographic key used to sign the JWT tokens
6. `GOOGLE_OAUTH2_CLIENT_ID`: client ID of your application
7. `GOOGLE_OAUTH2_CLIENT_SECRET`: Google's API key to perform OAuth2 workflow

To obtain the last 2 variables, you will need to visit [Google Console](https://console.google.com)
and create an OAuth2 token. Follow [the steps](https://developers.google.com/identity/protocols/oauth2)
by Google.

An example of my config:
```
MONGODB_DATABASE=store
MONGODB_USERNAME=root
MONGODB_PASSWORD=7skk7QENyt4ZA3YCarktQdp6M4d6wykx
MONGODB_URL=mongodb://root:7skk7QENyt4ZA3YCarktQdp6M4d6wykx@mongodb:27017/store?authSource=admin
MONGODB_TESTING_URI=mongodb://root:@mongodb:27017/testing?authSource=admin
JWT_SINGING_KEY=EH5Rnxg9KsdSu283ruRGtHaS2qNUCxhc
# OAuth2 API is hidden for security purposes
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
# Local development
## MongoDB installation 
If you prefer to run the application locally, you will need Maven and MongoDB.
Follow [the instructions](https://www.mongodb.com/docs/manual/installation/) for
your platform. Note that MongoDB since version 5 requires [AVX](https://en.wikipedia.org/wiki/Advanced_Vector_Extensions)
to run, so if you don't have that, stick with version 4. 

To start the server, you will need to start MongoDB process and connect to it. 
```commandline
mongod --port 27017
```
If the command above fails, it's likely it cannot access the data directory where it
stores the database artefacts, either because it doesn't exist or because it doesn't have
the permissions to read and write to it. On Linux, MongoDB uses `/data/db` directory, and on
Windows it's `C:\data\db`. To solve this issue, you can either create a custom directory 
and pass it to the `--dbpath` option, for example:
```commandline
mkdir -p ~/.db/RoyalStore
mongodb --dbpath ~/.db/RoyalStore --port 27017
```
creates a directory `.db/RoyalStore` in your user's home directory where the database will
be saved, and it starts the database using that directory. Alternatively, you may setup the 
default destination and avoid specifying an extra flag:
```commandline
sudo mkdir -p /data/db
sudo chmod -R 0777 /data/db
mongod --port 27017
```
These commands create the `data/db` directory where the databases will be stored, and sets 
its permissions to read and write for every user, after which you can start MongoDB with `mongod`.
## Running and testing with Maven
Use Maven to install dependencies:
```commandline
mvn dependency:go-offline
```
and run the tests:
```commandline
mvn test
```
If Spring Boot server failed to start, verify MongoDB
is running. After you have verified all tests pass, you
can start the server locally:
```commandline
mvn package -DskipTests
```
Now you have a working server with exposed REST API that
you can query using `curl`, Postman or accept incoming requests.
Consult the `openapi.yaml` file for possible requests and responses.