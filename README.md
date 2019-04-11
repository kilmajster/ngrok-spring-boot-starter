# ngrok spring boot starter
[![Build Status](https://img.shields.io/travis/createam-labs/ngrok-spring-boot-starter/master.svg?logo=travis)](https://travis-ci.org/createam-labs/ngrok-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/github/createam-labs/ngrok-spring-boot-starter/badge.svg?branch=master)](https://coveralls.io/github/createam-labs/ngrok-spring-boot-starter?branch=master)
![GitHub last commit](https://img.shields.io/github/last-commit/createam-labs/ngrok-spring-boot-starter.svg)
[![StackShare](https://img.shields.io/badge/tech-stack-0690fa.svg?style=flat)](https://stackshare.io/createam-labs/ngrok-spring-boot-starter)

###### What is ngrok?
tldr; Ngrok can give you public URLs for exposing your local web server, in our case it will be tomcats `http://localhost:8080`. For simply usage account is not needed. For more details you can check out their [site](https://ngrok.com/)

###### What is this?
👨🏼‍💻 🔛🌐This starter allows you to exposing your local web server on the web by public URLs! So every time you want to share your application with your colleague, you'll be able to do it by sending him a link from the application logs!  
 
 Just like it was done in below screenshot taken from [sample application](https://github.com/createam-labs/createam-labs-sample-app):  
 
![](https://raw.githubusercontent.com/createam-labs/ngrok-spring-boot-starter/development/ngrok-sample-screenshot.png
)[]()


### dependency & build
To add dependency firstly you have to build artifact from sources. To to that you have to do following:

```bash
$ git clone https://github.com/createam-labs/ngrok-spring-boot-starter.git
Cloning into 'ngrok-spring-boot-starter'...
remote: Counting objects: 84, done.
remote: Compressing objects: 100% (54/54), done.
remote: Total 84 (delta 22), reused 68 (delta 12), pack-reused 0
Unpacking objects: 100% (84/84), done.

$ cd ngrok-spring-boot-starter

user@ngrok-spring-boot-starter $ mvn install -Dmaven.test.skip=true
[INFO] Scanning for projects...
[INFO]
[INFO] ---------< io.github.createam-labs:ngrok-spring-boot-starter >----------
[INFO] Building ngrok-spring-boot-starter 0.1a
[INFO] --------------------------------[ jar ]---------------------------------
```
After this `ngrok-spring-boot-starter` will be installed in your local `.m2` repository, and you should be able to use it, like it's described in the following step 👇

- maven:
```xml

<dependency>
  <groupId>io.github.createam-labs</groupId>
  <artifactId>ngrok-spring-boot-starter</artifactId>
  <version>0.1</version>
</dependency>

```
- or gradle:
```groovy

compile('io.github.createam-labs:ngrok-spring-boot-starter:0.1')

````

### configuration
###### required properties

```properties

ngrok.enabled=true

```

###### optional properties

```properties

# if you've got already running ngrok instance on non default port
ngrok.api.url=http://localhost:4040

# if you want to use ngrok directory location different than default, which are:
#  - for Windows C:\Users\user\.ngrok2
#  - for unix systems ~/.ngrok2

# if for some reason ngrok starting takes longer than really quick you can override time 
# for waiting for ngrok startup what should speed up printing public URLs in logs
ngrok.waitForStartup.millis=3000

# if for some reason ngrok binary file address has changed you can override it by property corresponding to your OS
ngrok.binary.windows=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip
ngrok.binary.osx=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip
ngrok.binary.linux=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip


```

So now, the starter will run ngrok http tunneling on specified `server.port`, which in spring boot, by default will be `8080`.  



✅ That's all 👏 🏆 🎉





<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>

        <!--dependencies-->
        <commons-lang3.version>3.8.1</commons-lang3.version>
        <commons-io.version>2.6</commons-io.version>
        <hibernate-validator.version>5.4.1.Final</hibernate-validator.version>
        <spring-cloud-contract-wiremock.version>2.0.3.RELEASE</spring-cloud-contract-wiremock.version>
        <jmockit.version>1.45</jmockit.version>

        <!--plugins-->
        <maven-compiler-plugin.version>3.7.0</maven-compiler-plugin.version>
        <cobertura-maven-plugin.version>2.7</cobertura-maven-plugin.version>
        <jacoco-maven-plugin.version>0.8.3</jacoco-maven-plugin.version>
        <coveralls-maven-plugin.version>4.3.0</coveralls-maven-plugin.version>
    </properties>








```properties



ngrok.api.url=http://localhost:4040

ngrok.binary.windows=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip
ngrok.binary.osx=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip
ngrok.binary.linux=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip

ngrok.waitForStartup.millis=3000

ngrok.directory=C:\\Users\\user\\Desktop\\ngrok
```
ngrok.enabled=false


ok




🆒 Be happy because of your spring boot application served in the web without difficult server configuration and completely for free! 😎 🤙
