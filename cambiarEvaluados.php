<?php

include'conexion.php';

$idw = $_POST['idw'];

$consulta = "UPDATE datos_wearables SET estado='1' WHERE idw='$idw'";

$resultado = mysqli_query($con, $consulta);

if ($resultado){
    echo " Proceso OK ";
} else {
    echo " Falla del proceso";
}


?>  