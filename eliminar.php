<?php

include'conexion.php';

if ($con) {

    $idw=$_POST['idw'];

    $consulta = "DELETE FROM estado WHERE idw = '$idw'";
    $resultado = mysqli_query($con, $consulta);

    if ($resultado){
        echo " Eliminado de la base de datos OK ";
    } else {
        echo " Falla al eliminar";
    }
    
    
} else {
    echo "Falla conexion con Base de datos ";   
}

?>