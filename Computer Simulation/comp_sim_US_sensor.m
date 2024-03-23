%% Sensor readings

x1 = [9,
8,
8,
9,
9,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8 ];

x2 = [ 8,
8,
8,
8,
8,
8,
8,
8,
8,
12,
13,
13,
12,
10,
13,
13,
14,
14,
13,
13,
10,
10,
13,
12,
11,
13,
11,
10,
10,
9,
10,
9,
8,
10,
9,
11,
11,
12,
10,
11,
11,
1110,
9,
12,
10,
11,
10,];

x3 = [ 11,
10,
8,
8,
8,
8,
8,
8,
8,
8,
8,
9,
12,
8,
8,
7,
7,
8,
7,
8,
9,
8,
8,
8,
8,
8,
8,
8,
8,
8,
9,
9,
8,
9,
8,
8,
9,
8,
7,
8,
9,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
7,
13,
13,
9,
8,
9,
10,
8,
9,
10,
9,
9,
9,
8,
8,
9,
8,
9,
9,
9,
8,
8,
9,
7,
11,
8,
7,
9,
8,
9,
11,
11,
11,
9,
8,
8,
8,
9,
8,
9,
9,
9,
9,
9,
9,
9,
9,
9,
10,
9,
10,
9,
10,
10,
10,
9,
9,
9,
9,
9,
8,
8,
11,
8,
7,
8,
9,
8,
8,
8,
10,
8,
8,
8,
8,
10,
8,
11,
10,
11,
8,
11,
10,
9,
12,
12,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
8,
10,
10,
11,
9,
9,
9,
10,
9,
8,
8,
9,
8,
7,
9,
10,
9,
8,
8,
10,
9,
9,
8,
8,
10,
9,
8,
7,
8,
8,
8,
8,
8,
8,
8,];

x4 = [ 8,
8,
8,
5,
5,
8,
8,
8,
8,
8,
7,
8,
8,
9,
8,
9,
9,
9,
9,
9,
10,
10,
9,
9,
9,
9,
10,
10,
10,
10,
9,
11,
11,
10,
11,
10,
10,
10,
11,
12,
11,
11,
11,
11,
11,
12,
12,
13,
12,
14,
14,
15,
14,
13,
13,
14,
14,
15,
15,
14,
15,
14,
14,
15,
15,
15,
15,
15,
15,
14,
15,
15,
16,
15,
15,
14,
15,
16,
16,
17,
16,
16,
16,
16,
19,
18,
17,
18,
17,
18,
17,
19,
18,
19,
18,
18,
18,
18,
18,
18,
18,
18,
18,
18,
18,
19,
19,
18,
18,
18,
18,
18,
];

x5 = [ 17,
17,
17,
17,
17,
17,
17,
18,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
16,
17,
17,
17,
17,
17,
18,
17,
17,
17,
17,
18,
17,
17,
17,
17,
17,
18,
16,
17,
17,
17,
18,
18,
20,
17,
17,
18,
18,
18,
18,
18,
17,
17,
17,
17,
17,
17,
17,
17,
17,
17,
];

%% End of sensor readings

% x1 - stable water level at 9.5 cm
% distance reading should be 8 cm

% x2 - water bottle is angles at about 45 degrees

% x3 - bottle is being shook and then stabilized

% x4 - water is consumed through a straw through till empty

% x5 - bottle is empty and trying to calibrate for maximum bottle height

% get length of data vectors
n1 = length(x1);
n2 = length(x2);
n3 = length(x3);
n4 = length(x4);
n5 = length(x5);

%% send data through median filter

% default window size = 3
y1 = medfilt1(x1);
y2 = medfilt1(x2);
y3 = medfilt1(x3);
y4 = medfilt1(x4);
y5 = medfilt1(x5);

% increase window size to 5
y1_5 = medfilt1(x1,5);
y2_5 = medfilt1(x2,5);
y3_5 = medfilt1(x3,5);
y4_5 = medfilt1(x4,5);
y5_5 = medfilt1(x5,5);

% increase window size to 7
y1_7 = medfilt1(x1,7);
y2_7 = medfilt1(x2,7);
y3_7 = medfilt1(x3,7);
y4_7 = medfilt1(x4,7);
y5_7 = medfilt1(x5,7);

% increase window size to 10
y1_10 = medfilt1(x1,10);
y2_10 = medfilt1(x2,10);
y3_10 = medfilt1(x3,10);
y4_10 = medfilt1(x4,10);
y5_10 = medfilt1(x5,10);

% use input data length as window size
y1_n = medfilt1(x1,n1);
y2_n = medfilt1(x2,n2);
y3_n = medfilt1(x3,n3);
y4_n = medfilt1(x4,n4);
y5_n = medfilt1(x5,n5);


%% visualize the filtered data

% window size = 3
figure(1);
subplot(5,1,1);
plot(x1, 'b-');
hold on;
grid on;
plot(y1, 'o-');
subplot(5,1,2);
plot(x2, 'b-');
hold on;
grid on;
plot(y2, 'o-');
subplot(5,1,3);
plot(x3, 'b-');
hold on;
grid on;
plot(y3, 'o-');
subplot(5,1,4);
plot(x4, 'b-');
hold on;
grid on;
plot(y4, 'o-');
subplot(5,1,5);
plot(x5, 'b-');
hold on;
grid on;
plot(y5, 'o-');

% window size = 5
figure(2);
subplot(5,1,1);
plot(x1, 'b-');
hold on;
grid on;
plot(y1_5, 'o-');
subplot(5,1,2);
plot(x2, 'b-');
hold on;
grid on;
plot(y2_5, 'o-');
subplot(5,1,3);
plot(x3, 'b-');
hold on;
grid on;
plot(y3_5, 'o-');
subplot(5,1,4);
plot(x4, 'b-');
hold on;
grid on;
plot(y4_5, 'o-');
subplot(5,1,5);
plot(x5, 'b-');
hold on;
grid on;
plot(y5_5, 'o-');

% window size = 7
figure(3);
subplot(5,1,1);
plot(x1, 'b-');
hold on;
grid on;
plot(y1_7, 'o-');
subplot(5,1,2);
plot(x2, 'b-');
hold on;
grid on;
plot(y2_7, 'o-');
subplot(5,1,3);
plot(x3, 'b-');
hold on;
grid on;
plot(y3_7, 'o-');
subplot(5,1,4);
plot(x4, 'b-');
hold on;
grid on;
plot(y4_7, 'o-');
subplot(5,1,5);
plot(x5, 'b-');
hold on;
grid on;
plot(y5_7, 'o-');

% window size = 10
figure(4);
subplot(5,1,1);
plot(x1, 'b-');
hold on;
grid on;
plot(y1_10, 'o-');
subplot(5,1,2);
plot(x2, 'b-');
hold on;
grid on;
plot(y2_10, 'o-');
subplot(5,1,3);
plot(x3, 'b-');
hold on;
grid on;
plot(y3_10, 'o-');
subplot(5,1,4);
plot(x4, 'b-');
hold on;
grid on;
plot(y4_10, 'o-');
subplot(5,1,5);
plot(x5, 'b-');
hold on;
grid on;
plot(y5_10, 'o-');

% window size = n
figure(5);
subplot(5,1,1);
plot(x1, 'b-');
hold on;
grid on;
plot(y1_n, 'o-');
subplot(5,1,2);
plot(x2, 'b-');
hold on;
grid on;
plot(y2_n, 'o-');
subplot(5,1,3);
plot(x3, 'b-');
hold on;
grid on;
plot(y3_n, 'o-');
subplot(5,1,4);
plot(x4, 'b-');
hold on;
grid on;
plot(y4_n, 'o-');
subplot(5,1,5);
plot(x5, 'b-');
hold on;
grid on;
plot(y5_n, 'o-');

%% get water level measurements using filtered distance

% define constants
WH_MAX = 16;
WH_OFF = 2;
WC_TOT = 700;

% window size = 3
[wh1, wl1, bc1, wc1] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y1);
[wh2, wl2, bc2, wc2] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y2);
[wh3, wl3, bc3, wc3] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y3);
[wh4, wl4, bc4, wc4] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y4);
[wh5, wl5, bc5, wc5] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y5);

% window size = 5
[wh1_5, wl1_5, bc1_5, wc1_5] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y1_5);
[wh2_5, wl2_5, bc2_5, wc2_5] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y2_5);
[wh3_5, wl3_5, bc3_5, wc3_5] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y3_5);
[wh4_5, wl4_5, bc4_5, wc4_5] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y4_5);
[wh5_5, wl5_5, bc5_5, wc5_5] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y5_5);

% window size = 7
[wh1_7, wl1_7, bc1_7, wc1_7] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y1_7);
[wh2_7, wl2_7, bc2_7, wc2_7] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y2_7);
[wh3_7, wl3_7, bc3_7, wc3_7] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y3_7);
[wh4_7, wl4_7, bc4_7, wc4_7] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y4_7);
[wh5_7, wl5_7, bc5_7, wc5_7] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y5_7);

% window size = 10
[wh1_10, wl1_10, bc1_10, wc1_10] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y1_10);
[wh2_10, wl2_10, bc2_10, wc2_10] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y2_10);
[wh3_10, wl3_10, bc3_10, wc3_10] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y3_10);
[wh4_10, wl4_10, bc4_10, wc4_10] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y4_10);
[wh5_10, wl5_10, bc5_10, wc5_10] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y5_10);

% window size = n
[wh1_n, wl1_n, bc1_n, wc1_n] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y1_n);
[wh2_n, wl2_n, bc2_n, wc2_n] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y2_n);
[wh3_n, wl3_n, bc3_n, wc3_n] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y3_n);
[wh4_n, wl4_n, bc4_n, wc4_n] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y4_n);
[wh5_n, wl5_n, bc5_n, wc5_n] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,y5_n);

%plot the water consumption

figure(6);
grid on;
subplot(5,1,1);
plot(wc1);
subplot(5,1,2);
plot(wc2);
subplot(5,1,3);
plot(wc3);
subplot(5,1,4);
plot(wc4);
subplot(5,1,5);
plot(wc5);

figure(7);
grid on;
subplot(5,1,1);
plot(wc1_5);
subplot(5,1,2);
plot(wc2_5);
subplot(5,1,3);
plot(wc3_5);
subplot(5,1,4);
plot(wc4_5);
subplot(5,1,5);
plot(wc5_5);

figure(8);
grid on;
subplot(5,1,1);
plot(wc1_7);
subplot(5,1,2);
plot(wc2_7);
subplot(5,1,3);
plot(wc3_7);
subplot(5,1,4);
plot(wc4_7);
subplot(5,1,5);
plot(wc5_7);

figure(9);
grid on;
subplot(5,1,1);
plot(wc1_10);
subplot(5,1,2);
plot(wc2_10);
subplot(5,1,3);
plot(wc3_10);
subplot(5,1,4);
plot(wc4_10);
subplot(5,1,5);
plot(wc5_10);

figure(10);
grid on;
subplot(5,1,1);
plot(wc1_n);
subplot(5,1,2);
plot(wc2_n);
subplot(5,1,3);
plot(wc3_n);
subplot(5,1,4);
plot(wc4_n);
subplot(5,1,5);
plot(wc5_n);


%% function to process water level measurements

function [waterHeight, waterLevel, bottleCapacity, waterConsumption] = processWaterLevel(WH_MAX,WH_OFF,WC_TOT,distance)

for index = distance
    waterHeight = WH_MAX + WH_OFF - index;
end

waterLevel = waterHeight ./ WH_MAX .* 100;

bottleCapacity = waterLevel ./ 100 * WC_TOT;

for i = bottleCapacity
    waterConsumption = WC_TOT - i;
end

end










