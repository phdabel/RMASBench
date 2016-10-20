#damping 0.0 start time 35 greedy yes
for num in {1..30}
do
	./start.sh -c fgmd-bms-t35-d00-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t35-d00-gyes
mv ./*.dat ./fgmd-bms-t35-d00-gyes/
cd ..
###

#damping 0.5 start time 35 greedy yes
for num in {1..30}
do
	./start.sh -c fgmd-bms-t35-d05-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t35-d05-gyes
mv ./*.dat ./fgmd-bms-t35-d05-gyes/
cd ..
###


#damping 0.9 start time 35 greedy yes
for num in {1..30}
do
	./start.sh -c fgmd-bms-t35-d09-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir fgmd-bms-t35-d09-gyes
mv ./*.dat ./fgmd-bms-t35-d09-gyes/
cd ..
###
