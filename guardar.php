<?php

include'conexion.php';

if ($con) {
    echo "Conexion con base de datos exitosa! ";
    
    if(isset($_POST['ax'])) {
        $ax = $_POST['ax'];
    }
    if(isset($_POST['ay'])) {
        $ay = $_POST['ay'];
    }
    if(isset($_POST['az'])) {
        $az = $_POST['az'];
    }
    if(isset($_POST['gx'])) { 
        $gx = $_POST['gx'];
    }
    if(isset($_POST['gy'])) { 
        $gy = $_POST['gy'];
    }
    if(isset($_POST['gz'])) { 
        $gz = $_POST['gz'];
    }

    if(isset($_POST['idw'])) { 
        $idw = $_POST['idw'];
    }

    $separador = "@";

    $lax = explode($separador, $ax);
    $lay = explode($separador, $ay);
    $laz = explode($separador, $az);
    $lgx = explode($separador, $gx);
    $lgy = explode($separador, $gy);
    $lgz = explode($separador, $gz);


    for ($i = 1; $i < count($lax)-1; $i++) {
        $consulta = "INSERT INTO datos_wearables( idw, ax, ay, az, gx, gy, gz, estado) VALUES ('$idw','$lax[$i]','$lay[$i]','$laz[$i]','$lgx[$i]','$lgy[$i]','$lgz[$i]','0')";
        $resultado = mysqli_query($con, $consulta);
    }

    if ($resultado){
            echo " Registo en base de datos OK! ";
        } else {
            echo " Falla! Registro BD";
        }
    
    
} else {
    echo "Falla en la conexion con Base de datos";   
}

?>