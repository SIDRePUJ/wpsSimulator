./bni_besa_simple.sh

rsync -avz --delete sistemas@wpsmain:/home/sistemas/experiments/ .
rsync -avz --delete sistemas@wps01:/home/sistemas/experiments/   .
rsync -avz --delete sistemas@wps02:/home/sistemas/experiments/   .
rsync -avz --delete sistemas@wps03:/home/sistemas/experiments/   .
