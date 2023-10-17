<?php

include'conexion.php';

if ($con) {

    $idw = $_POST['idw'];
    $encender = $_POST['encender'];
    
    if(isset($_POST['idw'])) {
        $idw = $_POST['idw'];
    }
    if(isset($_POST['encender'])) {
        $encender = $_POST['encender'];
    }

    $consulta = "UPDATE estado SET encender = '$encender' WHERE idw = '$idw'";
    $resultado = mysqli_query($con, $consulta);
 
    // if ($resultado){     
    //     echo "actualizacion exitosa"; 
    //  } else {
    //     echo " Falla! Registro BD";
    //  }
    echo $resultado;
    $con -> close();

} else {
    echo "Falla en la conexion con Base de datos";   
}

?>