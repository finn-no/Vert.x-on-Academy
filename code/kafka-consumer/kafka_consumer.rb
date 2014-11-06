require 'vertx'
include Vertx
require 'poseidon'

Vertx::set_timer(500) do
  loop do
    consumer = Poseidon::PartitionConsumer.new("my_test_consumer", "adm-internalmod1.finntech.no", 7794,
                                               "internal.pipeline.task", 0, :earliest_offset)
    messages = consumer.fetch

    messages.each do |message|
      sleep 1.0/2.0
      EventBus.publish 'message', message.value
    end
  end
end

EventBus.publish 'message', '{"project":"Varming up..."}'

