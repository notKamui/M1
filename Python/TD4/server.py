from xmlrpc.server import SimpleXMLRPCServer
import random

jurons = ["Truc", "machin", "bla", "blo"]

def jure():
    return random.choices(jurons)

def tous_les_jurons():
    return "\n".join(jurons)

server = SimpleXMLRPCServer(("localhost", 8080))
print("Listening on port 8080...")
server.register_function(jure, "jure")
server.register_function(tous_les_jurons, "tous_les_jurons")
server.serve_forever()
