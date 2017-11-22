import { mapValues } from 'lodash'
import { isJsonString } from './utils/StringUtils'

const rawConfig = window.config
const config = mapValues(rawConfig || {}, (value) =>
  isJsonString(value) ? JSON.parse(value) : value)

// URL this app is deployed to
export const appUrl = config.appUrl || ''

// Base URL for API requests
export const apiUrl = config.apiServerUrl || '/rest'

// Base URL for links to pages that are not part of this app
export const serverUrl = config.serverUrl || ''
