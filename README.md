# Minimum RSSI Fingerprints for Reliable Indoor Localization

## Overview
ZoozTester investigates the minimal number of Received Signal Strength Indicator (RSSI) fingerprints required for effective indoor localization. This project addresses the limitations of GPS in enclosed spaces and evaluates the potential for using minimal data points to achieve accurate localization using existing WiFi networks.

## Capstone - Video Link To Project
[Video Link](https://drive.google.com/file/d/1rGSRFR5_7J_M6lbS6WwAk0f_0UcLmhqj/view?usp=sharing)
## Research Objectives
- **Optimization of Data Requirements**: Determine the minimal number of RSSI fingerprints necessary for dependable indoor localization.
- **Cost-Effective and Efficient Indoor Navigation**: Use minimal data to reduce costs and enhance processing speed in indoor navigation systems.
- **Algorithmic Analysis**: Examine the influence of reduced data sets on the accuracy and reliability of positioning algorithms.

## Data Files
- `new_radio_map.json`: Updated dataset of WiFi fingerprints after data optimization experiments.
- `radio_map.json`: Original dataset of WiFi fingerprints used for initial tests.
- `training.json`: Dataset utilized for training the localization algorithm and validating results.

## Source Files
- `Fingerprint.java`: Handles the properties and operations related to WiFi fingerprints.
- `Locator.java`: Calculates positions based on minimal RSSI data inputs.
- `Main.java`: Main entry point that combines all components and starts the experiments.
- `PointF.java`: Facilitates the management of coordinate data essential for localization accuracy.

## Visualization Tool: ZoozMapper
[ZoozMapper](https://github.com/TomerMeidan/ZoozMapper) is a companion visualization tool that displays WiFi fingerprints on graphs of x and y axes. It enables filtering and manipulation of fingerprints to utilize different datasets for this project. This tool is essential for visualizing the effects of data minimization on localization precision.

## Getting Started
To run this project:
1. Ensure Java is installed and properly configured on your system.
2. Compile the source files using your preferred Java compiler.
3. Execute `Main.java` to begin the experiments and view the outcomes.
4. Use ZoozMapper for graphical analysis and further data manipulation.

## Contribution
Contributions from both academia and industry are welcome to refine and expand the methodologies and applications of this research. Please fork the repository, suggest improvements, and submit pull requests.
