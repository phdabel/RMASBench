#damping 0.0 start time 30 greedy no
for num in {1..30}
do
	./start.sh -c fgmd-bms-t30-d00-gno -m paris -s no-21-10-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t30-d00-gno
mv ./*.dat ./fgmd-bms-t30-d00-gno/
cd ..
###

#damping 0.5 start time 30 greedy no
for num in {1..30}
do
	./start.sh -c fgmd-bms-t30-d05-gno -m paris -s no-21-10-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t30-d05-gno
mv ./*.dat ./fgmd-bms-t30-d05-gno/
cd ..
###


#damping 0.9 start time 30 greedy no
for num in {1..30}
do
	./start.sh -c fgmd-bms-t30-d09-gno -m paris -s no-21-10-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t30-d09-gno
mv ./*.dat ./fgmd-bms-t30-d09-gno/
cd ..
###


#gyes

#damping 0.0 start time 30 greedy yes
for num in {1..30}
do
	./start.sh -c fgmd-bms-t30-d00-gyes -m paris -s no-21-10-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t30-d00-gyes
mv ./*.dat ./fgmd-bms-t30-d00-gyes/
cd ..
###

#damping 0.5 start time 30 greedy yes
for num in {1..30}
do
	./start.sh -c fgmd-bms-t30-d05-gyes -m paris -s no-21-10-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t30-d05-gyes
mv ./*.dat ./fgmd-bms-t30-d05-gyes/
cd ..
###


#damping 0.9 start time 30 greedy yes
for num in {1..30}
do
	./start.sh -c fgmd-bms-t30-d09-gyes -m paris -s no-21-10-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t30-d09-gyes
mv ./*.dat ./fgmd-bms-t30-d09-gyes/
cd ..
###
