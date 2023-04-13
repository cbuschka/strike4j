# strike4j - Inofficial java lib to interact with the Alesis strike pro drum module

### Disclaimer: This lib is NO OFFICIAL Alesis product, it is not verified by the manufacturer and COULD CAUSE DAMAGE to your device! Using this lib COULD VOID THE WARRANTY of your device! Do NOT use this lib for copyright infringements. USE AT YOUR OWN RISK!

## Requirements

* Github account
* Java 11
* Maven 3

## Usage with maven

* Generate a "github access token classic" with "read:packages" permission for at least public repos (upper right
  corner >  choose "Settings" from menu > "Developer settings" from bottom left sidebar > "Personal access tokens" > "
  Tokens (classic)" > check "read:packages" permission.)

* Optionally encrypt it:

```bash
mvn --encrypt-password GENERATEDTOKEN
```

* Add it to your maven ~/.m2/settings.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<settings xmlns="http://maven.apache.org/settings/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        ...
        <server>
            <id>strike4j-github-mvn-repo</id>
            <username>YOUR GITHUB USERNAME</username>
            <password>ENCRYPTEDTOKEN</password>
        </server>
        ...
    </servers>
</settings>
```

* Add the dependency to your pom.xml under dependencies:

```xml

<dependency>
    <groupId>io.github.cbuschka.strike4j</groupId>
    <artifactId>strike4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

* Add the repo to your pom.xml under repositories:

```xml

<repository>
    <id>strike4j-github-mvn-repo</id>
    <url>https://maven.pkg.github.com/cbuschka/strike4j</url>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
    <releases>
        <enabled>true</enabled>
    </releases>
</repository>
```

## License

Copyright (c) 2022-2023 by [Cornelius Buschka](https://github.com/cbuschka).

[Apache License, Version 2.0](./license.txt)

## Trademarks

Alesis is a registered trademark of inMusicBrands, LLC.
