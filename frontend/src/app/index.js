import 'babel-polyfill'
import 'es6-symbol/implement'
import React from 'react'
import ReactDOM from 'react-dom'
import { createStore, applyMiddleware, compose } from 'redux'
import thunk from 'redux-thunk'
import { createLogger } from 'redux-logger'
import { syncHistoryWithStore } from 'react-router-redux'
import WebFont from 'webfontloader'
import { apiMiddleware } from 'redux-api-middleware'
import rootReducer from './reducers'
import Root from './containers/Root'
import { useRouterHistory } from 'react-router'
import { createHistory } from 'history'
import { appUrl } from './config'

import './styles/style.less'

WebFont.load({
  google: {
    families: [
      'Source Sans Pro:200,400,600',
      'Source Code Pro:400,600'
    ]
  },
  timeout: 2000
})

const logger = createLogger({
  predicate: (getState, action) =>
    process.env && (process.env.NODE_ENV === 'development')
})

const finalCreateStore = compose(
  applyMiddleware(
    thunk,
    apiMiddleware,
    logger
  )
)(createStore)

// Call and assign the store with no initial state
const store = ((initialState) => {
  const store = finalCreateStore(rootReducer, initialState)
  if (module.hot) {
  // Enable Webpack hot module replacement for reducers
    module.hot.accept('./reducers', () => {
      const nextRootReducer = require('./reducers')
      store.replaceReducer(nextRootReducer)
    })
  }
  return store
})()

const history = useRouterHistory(createHistory)({
  basename: appUrl
})

const enhancedHistory = syncHistoryWithStore(history, store)

ReactDOM.render(
  <Root store={store} history={enhancedHistory} />,
  document.getElementById('root')
)
