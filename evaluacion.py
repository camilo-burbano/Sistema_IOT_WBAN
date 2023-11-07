#!"C:\Users\c\AppData\Local\Programs\Python\Python311\python.exe"

import math
from scipy.spatial.distance import euclidean
import pymysql
import numpy as np
import sys

from fastdtw import fastdtw
import pandas as pd
import os
from io import StringIO

# para que funcione los paquetes externos: eje: pymysql
sys.path.append(
    "C:\\Users\\c\\AppData\\Local\\Programs\\Python\\Python311\\Lib\\site-packages")

idw = sys.argv[1]
direccion = sys.argv[2]
# recibe de funcion EVALUAR DE APP MOVIL tres parametros idw, fecha_momento, izq o der


def analisis(datos, direccion):
    datos_escalados = np.array([])
    angulos_x = np.array([])
    angulos_y = np.array([])
    secuencia_x = np.array([])
    secuencia_y = np.array([])
    alpha = 0.98  
    beta = 0.02 
    ang_x_prev = 0.0
    ang_y_prev = 0.0
    for dato in datos:
        denominador = math.sqrt(pow(float(dato[1]), 2) + pow(float(dato[2]), 2))
    if denominador > 0:
        d1 = math.atan(float(dato[1]) / math.sqrt(pow(float(dato[1]), 2) + pow(float(dato[2]), 2))) * (180.0 / math.pi)
        d2 = math.atan(-float(dato[0]) / math.sqrt(pow(float(dato[1]), 2) + pow(float(dato[2]), 2))) * (180.0 / math.pi)
    else:
        d1 = 0
        d2 = 2
                                       
    accel_ang_x=d1
    accel_ang_y=d2
    ang_x = alpha*(ang_x_prev+(float(dato[3]) /131)*0.001) + beta*accel_ang_x;
    ang_y = alpha*(ang_y_prev+(float(dato[5]) /131)*0.001) + beta*accel_ang_y;
    ang_x_prev=ang_x;
    ang_y_prev=ang_y;

    angulos_x = np.append(angulos_x, ang_x)
    angulos_y = np.append(angulos_y, ang_y)

    t = np.arange(len(angulos_x))
    secuencia_x = list(zip(t, angulos_x))
    secuencia_y = list(zip(t, angulos_y))


# ---------------dtw
# # Crear secuencias
    if (direccion == "1"):
        experto_x = np.load('Experto1_IzquierdaSecuencia_x.npy')
        experto_y = np.load('Experto1_IzquierdaSecuencia_y.npy')
        valores = [292,584,876,1168]
        d = "Izquierda"
    elif (direccion == "2"):
        experto_x = np.load('Experto1_CentroSecuencia_x.npy')
        experto_y = np.load('Experto1_CentroSecuencia_y.npy')
        valores = [125,250,375,500]
        d = "Centro"
    elif (direccion == "3"):
        experto_x = np.load('Experto1_DerechaSecuencia_x.npy')
        experto_y = np.load('Experto1_DerechaSecuencia_y.npy')
        valores = [345,690,1035,1380]
        d = "Derecha"
    else:
        print('error al cargar los datos')

    x = experto_x
    xx = np.array([])
    for i in x:
        xx = np.append(xx,float(i[1]))
    xx = xx.reshape(len(xx), 1)

    y = experto_y
    yy = np.array([])
    for i in y:
        yy = np.append(yy,float(i[1]))
    yy = yy.reshape(len(yy), 1)

    dx = secuencia_x
    dx = np.array([])
    for i in dx:
        dx = np.append(dx,float(i[1]))
    dx = dx.reshape(len(dx), 1)

    dy = secuencia_y
    dy = np.array([])
    for i in dy:
        dy = np.append(dy,float(i[1]))
    dy = dy.reshape(len(dy), 1)

# -------------------------------------------------------------------------------
    distancia_x, path_x = fastdtw(xx, dx, dist=euclidean)
    distancia_y, path_y = fastdtw(yy, dy, dist=euclidean)
# -------------------------------------------------------------------------------
    
    distancia_xy = round(math.sqrt(pow(x,2)+pow(y,2)),2)

    if valores[0] >= distancia_xy:
        evaluacion = 'Excelente'
    elif valores[0] < distancia_xy and valores[1] >= distancia_xy:
        evaluacion = 'Sobresaliente'
    elif valores[1] < distancia_xy and valores[2] >= distancia_xy:
        evaluacion = 'Bueno'
    elif valores[2] < distancia_xy and valores[3] >= distancia_xy:
        evaluacion = 'Regular'
    else:
        evaluacion = 'Malo'
        
    return (evaluacion, d)


# ------------------------------------------------SQL--------------------------------------
try:
    miConexion = pymysql.connect(
        host='localhost', 
        user='root', 
        passwd='', 
        db='sistema_iot_wban')
    cur = miConexion.cursor()

    cur.execute(
        "SELECT ax, ay, az, gx, gy, gz FROM datos_wearables WHERE idw ='"+idw+"' AND estado = '0'")

    datos = cur.fetchall()
    dtw_distance, dire = analisis(datos, direccion)

    cur.execute("INSERT INTO resultados (idw, direccion,evaluacion) VALUES ('" +
                idw+"', '"+dire+"' ,'"+dtw_distance+"')")

except Exception as e:
    miConexion.rollback()
    print("error")
else:
    miConexion.commit()

cur.close()
miConexion.close()

# ------------------------------------FIN SQL----------------------------------------------

# #//retorne valor
print(dtw_distance)
# print(warp_path)
# print(max_value)
