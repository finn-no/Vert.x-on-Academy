# Vert.x
## lightweight, polyglot, non-blocking, event-driven application platform
<br/>
<br/>
#### **Stig Kleppe-Jørgensen**
FINN reise

Note:
Hei, jeg heter Stig og tenkte jeg skulle gi dere en kort innføring i et kult applikasjonsrammeverk som heter Vert.x.
Nå har ikke jeg jobbet med vertx før, hvis en ser bort fra denne presentasjonen, men jeg har sett det på konferanser
og lest om det og jeg har syntes det hørtes bra ut. Men jeg fikk jo aldri tid til å leke med det! Da kom jeg på at jeg
burde sette meg opp med et foredrag om vertx på Academy. Da ble jeg jo tvunget til å prøve det ut. Så nå får vi se
hvordan det går når jeg skal snakke om ting jeg ikke kan.

----

## Some features

* Lightweight
* Platform for microservices
* Distributed event bus
* Polyglot language support
* High performance

Note:
* Lett å sende meldinger mellom kjørende moduler, selv ut i browseren.
* Språk som kjører inne i JVM-en, javascript, ruby, python, clojure, scala, groovy
* Bruke det riktige verktøyet og så videre
* Raskere enn Node.js - skalerer både vertikalt (bruker alle kjernene) og horisontalt

--

## Some features, cont.

* Simple event-based programming model
* A small asynchronous core
  * Extended by modules
* Simple but not simplistic
  * A lot of power very close to the programmer
  * Configuration and boiler-plate is kept to a minimum

Note:
* Har den samme programmerings-modellen som Node
* Lett å utvide vha moduler
  * alle språkene implementert som moduler
  * moduler for å jobbe mot eksterne systemer (jms, memcached, metrics, db som postgresql, kafka, neo4j, cassandra, etc)
* Disse modulene lastes ned ved behov
* Ikke mer XML

----

## Verticle

* The execution unit of Vert.x
* A lot like an actor
* Communicate by message passing
* In any supported language

Note:
* Den minste kjørbare enheten i Vert.x
* Ligner på en actor som kjent fra Erlang eller Akka
* Verticles snakker sammen ved å sende meldinger på event bussen

--

### Verticles, cont.

* Single threaded
  * No need to think about concurrency problems
  * But must not block
* Separate class loaders

Note:
* Race conditions, legge på synchronized, etc er ikke noe å bry seg om lenger
* Siden det bare er en tråd, så må en ikke skrive kode som blokkerer
* Ved eksisterende kode som blokkerer, bruk spesielle verticles (worker verticles) som kjører på egne tråder.
* Kjør demo av raw verticles

----

## Event bus

* The nervous system of Vert.x
* Clustered/High availability
* Transient messages
* Point to point, pub/sub, request/response

Note:
* Dette er en meldingsbuss
* Det er Tim Fox som bl.a. står bak HornetQ og har jobbet med RabbitMQ som står bak Vert.x så en kan regne med at event bussen er solid.
* Vert.x bruker Hazelcast for å implementere event bussen
* Det er ikke innebygd persistering av meldinger
* Støtter alle typer meldingshåndtering, 1 til 1, 1 til mange og å svare tilbake til sender

--

<!-- .slide: data-background="img/eventbus.png" data-background-size="100%" -->

--

### Sending messages

```
def eb = vertx.eventBus

// Send a message every second

vertx.setPeriodic(1000) {
  eb.publish("pub-sub-address", "for-all")
}

vertx.setPeriodic(1000) {
  eb.send("ping-address", "ping!", { reply ->
    println "Received reply ${reply.body}"
  })
}
```

Note:
* Adressen en melding skal sendes til er bare en streng

--

### Retrieving messages

```
def eb = vertx.eventBus

eb.registerHandler("ping-address", { message ->
  println "Received message: ${message.body}"

  message.reply("pong!")
})
```

--

### Even in the browser

```
<script src="http://cdn.sockjs.org/sockjs-0.3.4.min.js">< /script>
<script src='vertxbus.js'>< /script>

<script>
    var eb = new vertx.EventBus('http://localhost:8080/eventbus');

    eb.onopen = function() {
      eb.registerHandler('some-address', function(message) {
        console.log('received a message: ' + JSON.stringify(message);
      });

      eb.send('some-address', {project: 'travel-flight', size: 1200});
    }
< /script>
```

Note:
* HA demo

--

### Also support sharing data

```
// Verticle 1
var filter = vertx.getMap('filter');

filter.put('value', value);

// Verticle 2
var filter = vertx.getMap('filter');

filter.get('value');

```

Note:
* Det er ikke alltid meldingssending er en god måte å løse et problem på
* Caching er et eksempel på det
* Vert.x støtter deling av data på tvers av verticles
* Ikke på tvers av JVMer ennå
* Immutable
* Maps
* Sets

----

## Packaging & deployment

* As raw or as a module
* Configuration in JSON

Note:
* 2 forskjellige måter å pakke kode som verticles
* Eventuell konfigurasjon blir lagt i en JSON fil og hentet ut ved et eget API

--

### Raw verticles

* "Scripts" in any language
* Compiled by Vert.x if needed when starting

Note:
* Som vist på den første demoen med python/java verticles
* Mest for demo-formål

--

### Module verticles

* A zip or a directory
* Contains a mod.json
* Can depend on other modules

Note:
* Må inneholde en mod.json som beskriver modulen, bl.a. hva som skal kjøres opp når modulen startes

--

### Module verticles, cont.

* Downloaded on demand
* Index at modulereg.vertx.io, binaries on Maven/Bintray
* Can create jar of a module

```
$ vertx fatjar no.finn~sender-groovy~0.1.0
$ java -jar sender-groovy-0.1.0-fat.jar
```

Note:
* For å slippe å deploye vertx på hver server så kan vertx lage en fatjar

----

## Running verticles

* `vertx run <script>`
* `vertx runmod <module>`
* Can deploy other verticles programmatically

```
var container = require('vertx/container');
var console = require('vertx/console');

container.deployVerticle('message_filter.js', function (err, deploy_id) {
    if (!err) {
        console.log('The ' + name + ' has been deployed with id = ' + deploy_id);
    } else {
        console.log('Deployment failed: ' + err.getMessage());
    }
});

```

--

## Running verticles, cont.
### Embedded

```
PlatformManager pm = PlatformLocator.factory.createPlatformManager();

JsonObject conf = new JsonObject().putString("foo", "wibble");

pm.deployModule("com.mycompany~my-module~1.0",
                conf, 10, (AsyncResult<String> asyncResult) -> {
    if (asyncResult.succeeded()) {
        System.out.println("Deployment ID is " + asyncResult.result());
    } else {
        asyncResult.cause().printStackTrace();
    }
});
```

Note:
* En kan også embedde vert.x inne i et eksisterende Java-system
* Bedre å kjøre via vertx run*
