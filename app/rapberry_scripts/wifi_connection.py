import network
import time

# class to make wifi connectivity easier
class Router:
    def __init__(router, ssid, password):
        router.ssid = ssid
        router.password = password
    
# define most used connections
connection_katty = Router("DIGI_40f280", "a7eebf0d")
connection_Hotspostkatty = Router("katty", "87654321")
connection_Hotspotroli = Router("Olah's Galaxy A72", "xevr4131")


def connectToWifi():
    wlan = network.WLAN(network.STA_IF)
    wlan.active(True)
    # try to connect to a wifi address using ssid and password
    wlan.connect(connection_Hotspotroli.ssid, connection_Hotspotroli.password)
     
    # Wait for connect or fail
    wait = 60
    while wait > 0:
        print(f'status of wifi is:{wlan.status()}')
        if wlan.status() < 0 or wlan.status() >= 3:
            print("we have a good status. Breaking while loop")
            break
        wait -= 1
        print('waiting for connection...')
        time.sleep(1)
    print(f'exited while loop with wifi status: {wlan.status()}')
    # Handle connection error
    if wlan.status() != 3:
        return False
    else:
        print('connected')
        print('IP: ', wlan.ifconfig()[0])
        return True
    
connectToWifi()