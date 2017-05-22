if [ "$#" -ne 2 ]; then
	echo "#### USAGE ./run.sh input_filename.txt output_filename.txt ####"
else
	javac Message.java MessageQueue.java Node.java Simulator.java GHS.java
	echo "a" > a.log
	rm -r *.log
	python input_converter.py $1 > temp.txt
	java GHS temp.txt > temp1.txt
	python output_converter.py temp1.txt > $2
fi