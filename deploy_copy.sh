./bni_besa_simple.sh

#scp -r * wpsim01:/wpsim
rsync -avz --delete . sistemas@wpsmain:/home/sistemas/wpsim
rsync -avz --delete . sistemas@wps01:/home/sistemas/wpsim
rsync -avz --delete . sistemas@wps02:/home/sistemas/wpsim
rsync -avz --delete . sistemas@wps03:/home/sistemas/wpsim
