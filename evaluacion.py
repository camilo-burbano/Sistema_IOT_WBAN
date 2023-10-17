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
# print(idwp)
# idw='w002'
# direccion=1
# recibe de funcion EVALUAR DE APP MOVIL tres parametros idw, fecha_momento, izq o der


def analisis(datos, direccion):
    datos_escalados = np.array([])
    angulos_x = np.array([])
    angulos_y = np.array([])
    secuencia_x = np.array([])
    secuencia_y = np.array([])

    alpha = 0.98  
    beta = 0.02 

    for dato in datos:
        accel_ang_x=math.atan(float(dato[1]) /math.sqrt(pow(float(dato[1]) ,2) + pow(float(dato[2]) ,2)))*(180.0/3.14)
        accel_ang_y=math.atan(-float(dato[0]) /math.sqrt(pow(float(dato[1]) ,2) + pow(float(dato[2]) ,2)))*(180.0/3.14)
        ang_x = alpha*(ang_x_prev+(float(dato[3]) /131)*0.001) + beta*accel_ang_x;
        ang_y = alpha*(ang_y_prev+(float(dato[5]) /131)*0.001) + beta*accel_ang_y;

        ang_x_prev=ang_x;
        ang_y_prev=ang_y;
        #muestra =math.sqrt((ang_x*ang_x)+(ang_y*ang_y)) #{ang_x,ang_y}              
        #datos_escalados = np.append(datos_escalados, muestra) 
        angulos_x = np.append(angulos_x, ang_x)
        angulos_y = np.append(angulos_y, ang_y)

    #for dato in datos:
    #    muestra = ((float(dato[0]) * (9.81/16384.0)) + (float(dato[1]) * (9.81/16384.0)) + (float(dato[2]) * (9.81/16384.0)) + (
    #        float(dato[3]) * (1000.0/8192.0)) + (float(dato[4]) * (1000.0/8192.0)) + (float(dato[5]) * (1000.0/8192.0)))/6
    #datos_escalados = np.append(datos_escalados, muestra)

    #datos_escalados = datos_escalados.reshape(len(datos_escalados), 1)
    t = np.arange(len(angulos_x))
    secuencia_x = list(zip(t, angulos_x))
    secuencia_y = list(zip(t, angulos_y))

# ---------------dtw
# # Crear secuencias
    x = datos_escalados

    if (direccion == "1"):
        y = np.load('datosReferenciaIzquierda.npy')
        d = "Izquierda"
    elif (direccion == "2"):
        y = np.load('datosReferenciaCentro.npy')
        d = "Centro"
    elif (direccion == "3"):
        y = np.load('datosReferenciaDerecha.npy')
        d = "Derecha"
    else:
        print('error al cargar los datos')


# -------------------------------------------------------------------------------
    dtw_distance, warp_path = fastdtw(x, y, dist=euclidean)
# -------------------------------------------------------------------------------
    return (str(dtw_distance), d)


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

# preprocesamiento: escalar valores, promedio


# #//retorne valor
print(dtw_distance)
# print(warp_path)
# print(max_value)
