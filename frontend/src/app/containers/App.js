import React, { Component } from 'react'
/**
 * TODO: use react-ally to identify accessibility issue
 * import a11y from 'react-a11y'
 */
import { connect } from 'react-redux'

/**
 * TODO: use react-ally to identify accessibility issue in dev mode
 * if (process.env.NODE_ENV === 'development') a11y(React)
 */

class App extends Component {
  render () {
    return (
      <div>
        Machine translation page
      </div>
    )
  }
}

export default connect()(App)
