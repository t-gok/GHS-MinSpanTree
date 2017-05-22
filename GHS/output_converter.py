import sys
import os

f = open(sys.argv[1],'r')
lines = f.readlines()
f.close()

MST = []

for line in lines:
	if(line.strip() == ""):
		continue;

	tokens = line.strip().split(" ")
	n1 = int(tokens[0])
	n2 = int(tokens[1])
	wt = int(tokens[2])

	if(n1 < n2):
		t = (n1,n2,wt)
		if t not in MST:
			MST.append(t)

	else:
		t = (n2,n1,wt)
		if t not in MST:
			MST.append(t)

# sort mst according to wt

MST = sorted(MST, key=lambda x: x[2])

for (s1,s2,s3) in MST:
	print str(s1)+","+str(s2)+":"+str(s3)

