/**
 * Couleurs validées (skill dataviz : node scripts/validate_palette.js).
 *
 * Or/Argent/Bronze est un trio "medal-tier" personnalisé (le gris pur de
 * l'argent échoue systématiquement au plancher de chroma OKLCH — il n'existe
 * pas de couleur "argentée" valide pour ce système). Trio retenu, validé en
 * paires adjacentes (contexte : segments empilés) sur les deux modes :
 *   #eda100 (or) · #2a78d6 (argent) · #eb6834 (bronze)  → ALL CHECKS PASS
 * Le contraste de l'or est en-dessous de 3:1 en mode clair (WARN) : mitigé
 * par les libellés directs, la légende et le tableau (jamais la couleur seule).
 *
 * Le bleu "magnitude" reprend la teinte séquentielle par défaut de la palette
 * (magnitude à teinte unique, jamais un dégradé arc-en-ciel).
 */
export const medalColors = {
  gold: '#eda100',
  silver: '#2a78d6',
  bronze: '#eb6834',
} as const

export const magnitudeColor = '#2a78d6'

export const chartText = {
  secondary: '#52514e',
  muted: '#898781',
  grid: '#e1e0d9',
  axis: '#c3c2b7',
  surface: '#fcfcfb',
} as const

export const medalLabels = {
  gold: 'Or',
  silver: 'Argent',
  bronze: 'Bronze',
} as const
