import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Provider } from 'react-redux'
import { Router, Route } from 'react-router'
import App from '../containers/App'
import Statistics from '../containers/Statistics'

export default class Root extends Component {
  static propTypes = {
    store: PropTypes.object.isRequired,
    history: PropTypes.object.isRequired
  }

  render () {
    const {
      store,
      history
    } = this.props
    return (
      <Provider store={store}>
        <Router history={history}>
          <Route component={App} >
            <Route path='statistics' component={Statistics} />
            <Route path='/' component={App} />
          </Route>
        </Router>
      </Provider>
    )
  }
}
