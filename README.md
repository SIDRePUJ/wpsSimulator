# WellProdSim       
                     
``` 
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 *  \ V  V / | |_) |\__ \ *    @since 2023                                  *
 *   \_/\_/  | .__/ |___/ *                                                 *
 *           | |          *    @author Jairo Serrano                        *
 *           |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================  
```

- Crea una carpeta para los proyectos e ingresa a ella:
```
mkdir SIDRePUJ
cd SIDRePUJ
```

- Descarga los proyectos relacionados de git:

```
git clone git@github.com:SIDRePUJ/KernelBESA.git
git clone git@github.com:SIDRePUJ/LocalBESA.git
git clone git@github.com:SIDRePUJ/RemoteBESA.git
git clone git@github.com:SIDRePUJ/RationalBESA.git
git clone git@github.com:SIDRePUJ/BDIBESA.git
git clone git@github.com:SIDRePUJ/eBDIBESA.git
```
Compilalos en Netbeans y genera los jar, ahora instala los jar tu maven local:

```
mvn install:install-file -Dfile=KernelBESA.jar -DgroupId=BESA -DartifactId=KernelBESA -Dversion=3.17 -Dpackaging=jar
mvn install:install-file -Dfile=LocalBESA/dist/LocalBESA.jar -DgroupId=BESA -DartifactId=LocalBESA -Dversion=3.17 -Dpackaging=jar
mvn install:install-file -Dfile=BDIBESA/dist/BDIBESA.jar -DgroupId=BESA -DartifactId=BDIBESA -Dversion=3.17 -Dpackaging=jar
mvn install:install-file -Dfile=RemoteBESA/dist/RemoteBESA.jar -DgroupId=BESA -DartifactId=RemoteBESA -Dversion=3.17 -Dpackaging=jar
mvn install:install-file -Dfile=RationalBESA/dist/RationalBESA.jar -DgroupId=BESA -DartifactId=RationalBESA -Dversion=3.17 -Dpackaging=jar
```

Ahora descarga el proyecto y abrelo en IntelliJ u otro IDE de tu preferencia:
```
git clone git@github.com:SIDRePUJ/wpsSimulator.git
```
