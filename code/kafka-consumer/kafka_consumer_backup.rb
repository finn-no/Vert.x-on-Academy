require 'vertx'
include Vertx
require 'poseidon'

$PROJECTS = ['travel-flight', 'oppdrag-db', 'finn_native_app_proxy', 'penger-cyberstats', 'puppetmanifests', 'firma-db']

Vertx::set_periodic(500) do
  project = $PROJECTS[Random.rand(0..5)]
  EventBus.publish 'message', "{\"project\": \"#{project}\"}"
end
