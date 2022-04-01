<h1 align="center">
    Ngrok Spring Boot Starter
    <br>
    <a href="https://github.com/kilmajster/ngrok-spring-boot-starter/actions">
        <img align="center" src="https://github.com/kilmajster/ngrok-spring-boot-starter/workflows/main/badge.svg" alt="CI status badge">
    </a>
    <img align="center" src="https://img.shields.io/github/last-commit/kilmajster/ngrok-spring-boot-starter.svg" alt="Github last commit badge">
    <img align="center" src="https://img.shields.io/maven-central/v/io.github.kilmajster/ngrok-spring-boot-starter?color=blue" alt="Maven Central" >
    <img align="center" src="https://img.shields.io/github/license/kilmajster/ngrok-spring-boot-starter?color=blue" alt="MIT license badge">
    <br>
    <a href="https://github.com/kilmajster/ngrok-spring-boot-starter/actions?query=workflow%3A%22CI+on+windows-latest%22">
        <img align="center" src="https://github.com/kilmajster/ngrok-spring-boot-starter/workflows/CI%20on%20windows-latest/badge.svg" alt="CI on Windows">
    </a>
    <a href="https://github.com/kilmajster/ngrok-spring-boot-starter/actions?query=workflow%3A%22CI+on+ubuntu-latest%22">
        <img align="center" src="https://github.com/kilmajster/ngrok-spring-boot-starter/workflows/CI%20on%20ubuntu-latest/badge.svg" alt="CI on Ubuntu">
    </a>
    <a href="https://github.com/kilmajster/ngrok-spring-boot-starter/actions?query=workflow%3A%22CI+on+macos-latest%22">
        <img align="center" src="https://github.com/kilmajster/ngrok-spring-boot-starter/workflows/CI%20on%20macos-latest/badge.svg" alt="CI on MacOS">
    </a>
</h1>
<p align="center">
    <img src="https://ngrok.com/static/img/overview.png" alt="ngrok overview">
</p>

> What is Ngrok?
>
> *tldr;* Ngrok can create a http tunnel and give you a public URL with redirection to
> specified port on your local machine, which in our case will be a standard springs `http://localhost:8080`
> or whatever you set as `server.port` springs property. For simply usage any account is not needed. Tunnels created with
> free version will be available for 2 hours, so it is a great tool for development and testing purposes!
> For more details you can check out their [site](https://ngrok.com/).

## What this starter gives to you?
This starter will automatically download Ngrok binary corresponding to your 
operating system (Windows, Linux, OSX or even Docker) and then cache it into `home_directory/.ngrok2`. 
Then every time you will run your Spring Boot application, Ngrok will 
automatically build http tunnel pointing to your springs web server, and you will get pretty logs 
with the remote links, just like it's done below 👇
<p align="center">
    <img src="/.github/demo.gif" alt="demo gif">
</p>

Code of demo application available [here](https://github.com/kilmajster/demo).

## Dependency
- maven:
```xml
<dependency>
  <groupId>io.github.kilmajster</groupId>
  <artifactId>ngrok-spring-boot-starter</artifactId>
  <version>0.3.7</version>
</dependency>
```

- or gradle:
```groovy
compile('io.github.kilmajster:ngrok-spring-boot-starter:0.3.7')
````

##  Configuration
### 🚀 Minimal configuration
#### `ngrok.enabled`
For simple http tunneling to springs default server port, **only one configuration property is required**. 
There are many ways to provide spring config, for `application.property` based one, it will be:
```properties
ngrok.enabled=true
```

#### `ngrok.authToken`
Ngrok requires `authToken` to be defined, to obtain one visit https://dashboard.ngrok.com/get-started/your-authtoken and then add it like below:
```properties
ngrok.authToken=<YOUR PERSONAL AUTH TOKEN>
```
If you got already configured auth token in your ngrok config file there is no need to define this property.

✅ **All done, configuration is ready!** 

> What will happen now?
>
> If you are using default spring configuration of server port, which is `8080`, then ngrok will 
> be downloaded, extracted and cached into user default directory(eg. `/home/user/.ngrok2`) and then executed
> on application startup, so final command executed in background as child process, should look like:
> ```bash
> /home/user/.ngrok2/ngrok http 8080
> ```
> if you are using different server port, it will be picked up automatically from `server.port` property.

### ⚙️ Advanced configuration
#### `ngrok.config` - ngrok configuration file(s)
If you want to start ngrok with configuration file or files, you can use `ngrok.config` property:
```properties
ngrok.config=/home/user/custom-ngrok-config.yml
```
or if you've got multiple configuration files, use semicolon (`;`) as separator, like below:
```properties
ngrok.config=/home/user/custom-ngrok-config.yml;/home/user/another-ngrok-config.yml
```
then generated ngrok command, should look like this:
```bash
/home/user/.ngrok2/ngrok http -config /home/user/custom-ngrok-config.yml 8080
# or for multiple configs, could be something like this:
/home/user/.ngrok2/ngrok http -config /home/user/custom-ngrok-config.yml -config /home/user/another-ngrok-config.yml 8080
```
###### configuration from Classpath
If you prefer to keep ngrok configuration file inside your app, just add it as resource file and prefix `ngrok.config` property
with `classpath:`, so for config in standard springs resources root dir `src/main/resources/ngrok.yml`, it should look like following:
```properties
ngrok.config=classpath:ngrok.yml
```

#### `ngrok.command` - ngrok custom command attributes
If you want to achieve something more complex, you can use `ngrok.command` property to provide ngrok execution attributes, 
example:
```properties
# to run default behavior
ngrok.command=http 8080
# should result with command = /home/user/.ngrok2/ngrok http 8000

# or some more specific
ngrok.command=http -region=us -hostname=dev.example.com 8000
# should be = /home/user/.ngrok2/ngrok http -region=us -hostname=dev.example.com 8000
```

##### Optional properties & descriptions
```properties
# if you've got already running Ngrok instance somewhere else, you can specify its host & port, whoch defaults are:
ngrok.host=http://127.0.0.1
ngrok.port=4040

# if you want to use Ngrok directory location different than default, which are:
#  - for Windows C:\Users\user\.ngrok2
#  - for unix systems ~/.ngrok2
# use ngrok.directory property, like below:
ngrok.directory=C:\\Users\\user\\Desktop\\ngrok

# if for some reason Ngrok starting takes longer than really quick, you can override time 
# of waiting for ngrok startup:
ngrok.waitForStartup.millis=3000

# if for some reason Ngrok binary file address has changed you can override it 
# by property corresponding to your OS
ngrok.binary.windows32=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip
ngrok.binary.linux32=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip
ngrok.binary.osx32=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip

ngrok.binary.windows=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-amd64.zip
ngrok.binary.linux=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip
ngrok.binary.osx=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-amd64.zip

# or just specify custom location as fallback:
ngrok.binary.custom=http://not-exist.com/custom-ngrok-platform-bsd-arm-sth.zip
```

#### Issues & contributing
If you've got any troubles or ideas, feel free to report an issue or create pull request with improvements 🙂.

#### References
 - https://ngrok.com/
 - https://spring.io/projects/spring-boot
 - https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-starter

