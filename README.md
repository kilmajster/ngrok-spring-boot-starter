# ngrok spring boot starter
[![Travis build Status](https://img.shields.io/travis/kilmajster/ngrok-spring-boot-starter/master.svg?logo=travis)](https://travis-ci.org/kilmajster/ngrok-spring-boot-starter)
![GitHub last commit](https://img.shields.io/github/last-commit/kilmajster/ngrok-spring-boot-starter.svg)
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=http%3A%2F%2Fcentral.maven.org%2Fmaven2%2Fio%2Fgithub%2Fkilmajster%2Fngrok-spring-boot-starter%2Fmaven-metadata.xml)
[![StackShare](https://img.shields.io/badge/tech-stack-0690fa.svg?style=flat)](https://stackshare.io/createam-labs/ngrok-spring-boot-starter)

![Gif with logs that contains public ngrok URLs](https://raw.githubusercontent.com/kilmajster/ngrok-spring-boot-starter/master/ngrok.gif)

Code of demo application available [here](https://github.com/kilmajster/demo).

##### What is ngrok?
*tldr;* Ngrok can create a http tunnel and give you a public URL with redirection to 
specified port on your local machine, which in our case will be a standard springs `http://localhost:8080` 
or whatever you set as `server.port`. For simply usage account is not needed. Tunnels created with 
free version will be available for 8 hours, so it is great tool for development and testing purposes! 
For more details you can check out their [site](https://ngrok.com/).

###### Paid version
If you want to use this starter with paid ngrok version, you just have to set property, which determinate proper 
configured ngrok binary location, ex. ```ngrok.directory=/Users/user/custom/location```

### What this starter gives to you?
This starter will automatically download ngrok binary corresponding to your OS and cached it in 
your ```home directory/.ngrok2```. Then every time you will run your Spring Boot application, ngrok will 
be automatically build http tunnel pointing to your springs web server and you will get pretty logs 
with the link, just like it's done below 👇

![](https://raw.githubusercontent.com/kilmajster/ngrok-spring-boot-starter/master/demo.png)


### Dependency
- maven:
```xml
<dependency>
  <groupId>io.github.kilmajster</groupId>
  <artifactId>ngrok-spring-boot-starter</artifactId>
  <version>0.1</version>
</dependency>
```

- or gradle:
```groovy
compile('io.github.kilmajster:ngrok-spring-boot-starter:0.1')
````

### 🛠 Configuration 
##### Required ```application.properties```
```properties
ngrok.enabled=true
```

###### Optional ```application.properties``` & descriptions
```
# if you've got already running ngrok instance on non default port
ngrok.api.url=http://localhost:4040

# if you want to use ngrok directory location different than default, which are:
#  - for Windows C:\Users\user\.ngrok2
#  - for unix systems ~/.ngrok2
ngrok.directory=C:\\Users\\user\\Desktop\\ngrok

# if for some reason ngrok starting takes longer than really quick, you can override time 
# of waiting for ngrok startup
ngrok.waitForStartup.millis=3000

# if for some reason ngrok binary file address has changed you can override it 
# by property corresponding to your OS
ngrok.binary.windows32=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip
ngrok.binary.linux32=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip
ngrok.binary.osx32=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip

ngrok.binary.windows=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-amd64.zip
ngrok.binary.windows=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip
ngrok.binary.osx=https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-amd64.zip

# in case of trouble, you can specify the custom ngrok binary zip archive url by:
ngrok.binary.custom=http://not-exist.com/custom-ngrok-platform-bsd-arm-sth.zip
```

So now, the starter will run ngrok http tunneling on specified `server.port`, which in spring boot, by default will be `8080`.  

✅ That's all

#### Issues & contributing
If you've got any troubles or ideas, feel free to report an issue or create pull request with improvements 🙂.

#### References
 - https://ngrok.com/
 - https://spring.io/projects/spring-boot
 - https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-starter


