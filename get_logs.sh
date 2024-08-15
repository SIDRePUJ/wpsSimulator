mkdir -p /home/sistemas/logs/wpsmain /home/sistemas/logs/wps01 /home/sistemas/logs/wps02 /home/sistemas/logs/wps03

cd /home/sistemas/logs/wpsmain
rsync -avz --delete sistemas@wpsmain:/home/sistemas/experiments/ /home/sistemas/logs/wpsmain
cd /home/sistemas/logs/wps01
rsync -avz --delete sistemas@wps01:/home/sistemas/experiments/  /home/sistemas/logs/wps01
cd /home/sistemas/logs/wps02
rsync -avz --delete sistemas@wps02:/home/sistemas/experiments/ /home/sistemas/logs/wps02
cd /home/sistemas/logs/wps03
rsync -avz --delete sistemas@wps03:/home/sistemas/experiments/ /home/sistemas/logs/wps03