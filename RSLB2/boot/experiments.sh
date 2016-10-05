#damping 0.0 start time 30 greedy no & yes interteam no & yes
#for num in {1..30}
#do
#	./start.sh -c mtta-bms-itno-t30-d00-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-itno-t30-d00-gno
#mv ./*.dat ./mtta-bms-itno-t30-d00-gno/
#cd ..
###

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-itno-t30-d00-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-itno-t30-d00-gyes
#mv ./*.dat ./mtta-bms-itno-t30-d00-gyes/
#cd ..
###

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-ityes-t30-d00-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-ityes-t30-d00-gno
#mv ./*.dat ./mtta-bms-ityes-t30-d00-gno/
#cd ..
###

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-ityes-t30-d00-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-ityes-t30-d00-gyes
#mv ./*.dat ./mtta-bms-ityes-t30-d00-gyes/
#cd ..
###

#damping 0.5 start time 30 greedy no & yes interteam no & yes
#for num in {1..30}
#do
#	./start.sh -c mtta-bms-itno-t30-d05-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-itno-t30-d05-gno
#mv ./*.dat ./mtta-bms-itno-t30-d05-gno/
#cd ..
###

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-itno-t30-d05-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-itno-t30-d05-gyes
#mv ./*.dat ./mtta-bms-itno-t30-d05-gyes/
#cd ..
###

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-ityes-t30-d05-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-ityes-t30-d05-gno
#mv ./*.dat ./mtta-bms-ityes-t30-d05-gno/
#cd ..
###

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-ityes-t30-d05-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-ityes-t30-d05-gyes
#mv ./*.dat ./mtta-bms-ityes-t30-d05-gyes/
#cd ..
###

#damping 0.9 start time 30 greedy no & yes interteam no & yes
#for num in {1..30}
#do
#	./start.sh -c mtta-bms-itno-t30-d09-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-itno-t30-d09-gno
#mv ./*.dat ./mtta-bms-itno-t30-d09-gno/
#cd ..
###

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-itno-t30-d09-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#cd results
#mkdir mtta-bms-itno-t30-d09-gyes
#mv ./*.dat ./mtta-bms-itno-t30-d09-gyes/
#cd ..
###

for num in {1..30}
do
	./start.sh -c mtta-bms-ityes-t30-d09-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-ityes-t30-d09-gno
mv ./*.dat ./mtta-bms-ityes-t30-d09-gno/
cd ..
###


for num in {1..30}
do
	./start.sh -c mtta-bms-ityes-t30-d09-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-ityes-t30-d09-gyes
mv ./*.dat ./mtta-bms-ityes-t30-d09-gyes/
cd ..
###


#damping 0.0 start time 35 greedy no & yes interteam no & yes
for num in {1..30}
do
	./start.sh -c mtta-bms-itno-t35-d00-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-itno-t35-d00-gno
mv ./*.dat ./mtta-bms-itno-t35-d00-gno/
cd ..
###

for num in {1..30}
do
	./start.sh -c mtta-bms-itno-t35-d00-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-itno-t35-d00-gyes
mv ./*.dat ./mtta-bms-itno-t35-d00-gyes/
cd ..
###

for num in {1..30}
do
	./start.sh -c mtta-bms-ityes-t35-d00-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-ityes-t35-d00-gno
mv ./*.dat ./mtta-bms-ityes-t35-d00-gno/
cd ..
###

for num in {1..30}
do
	./start.sh -c mtta-bms-ityes-t35-d00-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-ityes-t35-d00-gyes
mv ./*.dat ./mtta-bms-ityes-t35-d00-gyes/
cd ..
###

#damping 0.5 start time 35 greedy no & yes interteam no & yes
#for num in {1..30}
#do
#	./start.sh -c mtta-bms-itno-t35-d05-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-itno-t35-d05-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-ityes-t35-d05-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#for num in {1..30}
#do
#	./start.sh -c mtta-bms-ityes-t35-d05-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
#done

#damping 0.9 start time 35 greedy no & yes interteam no & yes
for num in {1..30}
do
	./start.sh -c mtta-bms-itno-t35-d09-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-itno-t35-d09-gno
mv ./*.dat ./mtta-bms-itno-t35-d09-gno/
cd ..
###

for num in {1..30}
do
	./start.sh -c mtta-bms-itno-t35-d09-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-itno-t35-d09-gyes
mv ./*.dat ./mtta-bms-itno-t35-d09-gyes/
cd ..
###

for num in {1..30}
do
	./start.sh -c mtta-bms-ityes-t35-d09-gno -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-ityes-t35-d09-gno
mv ./*.dat ./mtta-bms-ityes-t35-d09-gno/
cd ..
###

for num in {1..30}
do
	./start.sh -c mtta-bms-ityes-t35-d09-gyes -m paris -s no-27-15-2013-rblockades -b --seed $num
done

cd results
mkdir mtta-bms-ityes-t35-d09-gyes
mv ./*.dat ./mtta-bms-ityes-t35-d09-gyes/
cd ..
