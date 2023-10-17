<?php

include'conexion.php';

if ($con) {
    
    if(isset($_POST['idw'])) {
      $idw = $_POST['idw'];
    }

    $consulta = "SELECT encender FROM estado WHERE idw = '$idw'";
    $resultado = mysqli_query($con, $consulta);
   
    $dato=mysqli_fetch_array($resultado, MYSQLI_NUM);


    if ($resultado){
        
        echo $dato[0];
        
    } else {
        echo " Falla! Registro BD";
    }
    $con -> close();
    
} else {
    echo "Falla! conexion con Base de datos ";   
}

?>