package com.andtree.vdsina.data.model.serverstat

import co.yml.charts.common.model.Point

data class ChartsPoints(
    val cpuPoints: List<Point>,
    val diskReadsPoints: List<Point>,
    val diskWritesPoints: List<Point>
)
