# ngrok spring boot starter

This starter allows you to publish your locally running spring boot application to the Web! 👨🏼‍💻 🔛🌐️ So every time you want to
 share your application with your colleague, you'll be able to do it by sending him a link from the application logs!  
 
 Just like it was done in below screenshot taken from [sample application](https://github.com/createam-labs/createam-labs-sample-app):  
 
![](https://raw.githubusercontent.com/createam-labs/ngrok-spring-boot-starter/development/ngrok-sample-screenshot.png
)[]()


## Configuration
### ngrok configuration
If you haven't heard what is ngrok yet, you can check out their [site](https://ngrok.com/) (tldr; _ngrok can proxy traffic from web to your local machine on the specified port, in our case it will be tomcats_ `http://localhost:8080`). How to create account, download, install & configure ngrok is described in their short [guide](https://dashboard.ngrok.com/get-started) 📄.
  Remember where you extracting binary file because this will be important in next step 🔜

### add dependency (not working yet)
- maven:
```xml

<dependency>
  <groupId>io.github.createam-labs</groupId>
  <artifactIdngrok->ngrok-spring-boot-starter</artifactId>
  <version>0.1a</version>
</dependency>

```
- or gradle:
```groovy

compile('io.github.createam-labs:ngrok-spring-boot-starter:0.1a')

````
- alternative solution (works before release to maven central repository)
Open terminal and do the following:
```bash
$ git clone https://github.com/createam-labs/ngrok-spring-boot-starter.git

Cloning into 'ngrok-spring-boot-starter'...
remote: Counting objects: 84, done.
remote: Compressing objects: 100% (54/54), done.
remote: Total 84 (delta 22), reused 68 (delta 12), pack-reused 0
Unpacking objects: 100% (84/84), done.

$ cd ngrok-spring-boot-starter

$ mvn clean install

```

### spring boot (auto)configuration
In your `application.properties` file add following (_this works on Windows 10_):
```properties

# absolute path to the binary file from previous step 👌
ngrok.path=C:\\dev\\ngrok\\ngrok.exe

```
So now, the starter will run ngrok http tunneling on specified `server.port`, which in spring boot, by default will be `8080`.
