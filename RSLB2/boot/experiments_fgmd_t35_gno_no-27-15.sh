#damping 0.0 start time 35 greedy no
for num in {1..30}
do
	./start.sh -c fgmd-bms-t35-d00-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t35-d00-gno
mv ./*.dat ./fgmd-bms-t35-d00-gno/
cd ..
###

#damping 0.5 start time 35 greedy no
for num in {1..30}
do
	./start.sh -c fgmd-bms-t35-d05-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t35-d05-gno
mv ./*.dat ./fgmd-bms-t35-d05-gno/
cd ..
###


#damping 0.9 start time 35 greedy no
for num in {1..30}
do
	./start.sh -c fgmd-bms-t35-d09-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t35-d09-gno
mv ./*.dat ./fgmd-bms-t35-d09-gno/
cd ..
###
