<?php

include 'conexion.php';

if ($con) {

    $idw = $_GET['codigo'];

    $consulta = "SELECT * FROM estado WHERE idw = '$idw'";
    $resultado = mysqli_query($con, $consulta);

    while ($fila = $resultado->fetch_array()) {
        $datos[] = array_map('utf8_encode', $fila);
    }

    echo json_encode($datos);
    $resultado->close();

} else {
    echo "Falla en la conexion con Base de datos";
}

?>