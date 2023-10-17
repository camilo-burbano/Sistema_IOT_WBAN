<?php

$idw = $_POST['idw'];
$direccion = $_POST['direccion'];

if (isset($_POST['idw'])) {
    $idw = $_POST['idw'];
}

if (isset($_POST['encender'])) {
    $direccion = $_POST['direccion'];
}

// ob_start();
// system("C:\\xampp\\htdocs\\sistemaIoTWBAN\\evaluacion.py $idw ");
// $res = ob_get_contents();
// ob_end_clean(); 

$res = array();
exec("python C:\\xampp\\htdocs\\sistemaIoTWBAN\\evaluacion.py $idw $direccion ", $res);
echo $res[0];

?>