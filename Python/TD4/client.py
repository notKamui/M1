from xmlrpc.client import ServerProxy

with ServerProxy("http://localhost:8080") as proxy:
    print(proxy.jure())
    print()
    print(proxy.tous_les_jurons())
