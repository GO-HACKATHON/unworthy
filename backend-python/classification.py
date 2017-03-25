import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf

dataframe = pd.read_csv("data_training.csv")
dataframe = dataframe.drop(["label"], axis=1)
dataframe = dataframe[0:10]

dataframe.loc[:,("y1")] = [1, 1, 1, 0, 0, 1, 0, 1, 1, 1] # CUMA 10 DATA TRAINING :p
dataframe.loc[:,("y2")] = dataframe["y1"] == 0
dataframe.loc[:,("y2")] = dataframe["y2"].astype(int)

print(dataframe)

inputX = dataframe.loc[:, ['speed', 'weather']].as_matrix()
inputY = dataframe.loc[:, ["y1", "y2"]].as_matrix()
learning_rate = 0.000001
training_epochs = 2000
display_step = 20
n_samples = inputY.size
x = tf.placeholder(tf.float32, [None, 2])
W = tf.Variable(tf.zeros([2, 2]))
b = tf.Variable(tf.zeros([2]))
y_values = tf.add(tf.matmul(x, W), b)
y = tf.nn.softmax(y_values)
y_ = tf.placeholder(tf.float32, [None, 2])
cost = tf.reduce_sum(tf.pow(y_ - y, 2))/(2*n_samples)
optimizer = tf.train.GradientDescentOptimizer(learning_rate).minimize(cost)
init = tf.global_variables_initializer()
sess = tf.Session()
sess.run(init)
for i in range(training_epochs):
    sess.run(optimizer, feed_dict={x: inputX, y_: inputY})

print(sess.run(y, feed_dict={x: inputX }))
print(sess.run(tf.nn.softmax([1., 2.])))

# Belom Akurat :(