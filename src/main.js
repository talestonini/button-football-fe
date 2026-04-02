// Import our custom CSS
import './scss/styles.scss'

// Import all of Bootstrap's JS
import * as bootstrap from 'bootstrap'

// Import Chart.js and register required components
import {
  Chart,
  CategoryScale,
  LinearScale,
  BarController,
  BarElement
} from 'chart.js'

// Register the components
Chart.register(
  CategoryScale,
  LinearScale,
  BarController,
  BarElement
)

import 'scalajs:main.js'
