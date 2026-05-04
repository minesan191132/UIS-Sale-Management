import { ref, onBeforeUnmount } from 'vue'
import { API_BASE_URL } from './api'

/**
 * Vue 3 composable that connects to the backend SSE endpoint and
 * fires a callback whenever an ORDER_UPDATED event arrives.
 *
 * Usage:
 *   const { connected } = useSseOrderUpdates(() => {
 *     // reload your data here
 *   })
 */
export function useSseOrderUpdates(onOrderUpdated) {
  const connected = ref(false)
  let eventSource = null
  let reconnectTimer = null
  const MAX_RECONNECT_DELAY = 30_000
  let reconnectDelay = 2_000

  const connect = () => {
    // Build SSE URL from the API base (strip trailing /api to get server root)
    const baseUrl = API_BASE_URL.replace(/\/api\/?$/, '')
    const sseUrl = `${baseUrl}/api/sse/subscribe`

    try {
      eventSource = new EventSource(sseUrl)

      eventSource.addEventListener('INIT', () => {
        connected.value = true
        reconnectDelay = 2_000 // reset on successful connect
        console.log('[SSE] Connected to', sseUrl)
      })

      eventSource.addEventListener('ORDER_UPDATED', (event) => {
        console.log('[SSE] ORDER_UPDATED received:', event.data)
        if (typeof onOrderUpdated === 'function') {
          onOrderUpdated(event.data)
        }
      })

      eventSource.onerror = () => {
        connected.value = false
        eventSource?.close()
        eventSource = null
        scheduleReconnect()
      }
    } catch (err) {
      console.warn('[SSE] Failed to create EventSource:', err)
      scheduleReconnect()
    }
  }

  const scheduleReconnect = () => {
    if (reconnectTimer) return
    console.log(`[SSE] Reconnecting in ${reconnectDelay / 1000}s...`)
    reconnectTimer = setTimeout(() => {
      reconnectTimer = null
      reconnectDelay = Math.min(reconnectDelay * 1.5, MAX_RECONNECT_DELAY)
      connect()
    }, reconnectDelay)
  }

  const disconnect = () => {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }
    connected.value = false
  }

  // Auto-connect on composable creation
  connect()

  // Auto-disconnect when the component unmounts
  onBeforeUnmount(() => {
    disconnect()
  })

  return { connected, disconnect }
}
