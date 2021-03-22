import certifi
from importlib import reload 
print("it works!")

import os
import site
from setuptools.command import easy_install
install_path = os.environ['GLUE_INSTALLATION']
easy_install.main( ["--install-dir", install_path, "https://files.pythonhosted.org/packages/ce/db/e55f6cf13251880434ca74cc77da70fc4f9336875f88556ce7de39cf9eab/confluent_kafka-1.6.0-cp36-cp36m-manylinux2010_x86_64.whl"] )
reload(site)

from confluent_kafka import Producer

print(certifi.where())

# Make sure you have 
# 1. create 'confluent-cloud-kafka-topic' in Confluent Cloud.
# 2. create service account with correct ACL so that Client App can it.
# 3. create API key and secret that are associated with the created service account in Confluent Cloud.

p = Producer({
    'bootstrap.servers': 'borker_url',
    'sasl.mechanism': 'PLAIN',
    'security.protocol': 'SASL_SSL',
    'ssl.ca.location': certifi.where(),
    'sasl.username': 'API_key',
    'sasl.password': 'Secret',

})
# the default ssl.ca.location is "'ssl.ca.location': '/usr/local/lib/python3.6/site-packages/certifi/cacert.pem'" in the Glue Run-time environment.

def acked(err, msg):
    """Delivery report callback called (from flush()) on successful or failed delivery of the message."""
    if err is not None:
        print("failed to deliver message: {}".format(err.str()))
    else:
        print("produced to: {} [{}] @ {}".format(msg.topic(), msg.partition(), msg.offset()))


for i in range(500):
	p.produce('confluent-cloud-kafka-topic', value='python test value', callback=acked)

p.flush(10)

# The End.