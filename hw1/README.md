Test data is stored in folder data.

Generated outputs are stored in output. Files with `vpt` are for visualizing in VPT tool.

File `src\main\java\interpolate.java` is for reading data from stdin and it outputs 32bit floats on stdout.

File `src\main\java\visualize.java` is for transforming output data to image format for VPT tool.

Example of command to rune the code:
```
cat data/input10k.txt | java -jar out/artifacts/hw1_jar/hw1.jar --method modified --r 0.5 --min-x -4.0 --min-y 0.0 --min-z -1.6 --max-x 0.5 --max-y 4.0 --max-z 1.75 --res-x 128 --res-y 128 --res-z 128 > output/output_10k_128_r05-2.raw
```