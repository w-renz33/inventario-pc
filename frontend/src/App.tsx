import { Navigate, Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import { ProtectedRoute } from './auth/ProtectedRoute';
import { LoginPage } from './pages/LoginPage';
import { ProductosPage } from './pages/ProductosPage';
import { IngresosPage } from './pages/IngresosPage';
import { KardexPage } from './pages/KardexPage';
import { ProveedoresPage } from './pages/ProveedoresPage';
import { DashboardPage } from './pages/DashboardPage';

/** Rutas login -> productos -> ingreso -> kardex con guarda de sesion.
 *  Logo/sidebar navegan con <Link> (sin href="#" muertos de Stitch). */
export default function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route
            path="/dashboard"
            element={<ProtectedRoute><DashboardPage /></ProtectedRoute>}
          />
          <Route
            path="/productos"
            element={<ProtectedRoute><ProductosPage /></ProtectedRoute>}
          />
          <Route
            path="/ingresos"
            element={<ProtectedRoute><IngresosPage /></ProtectedRoute>}
          />
          <Route
            path="/kardex"
            element={<ProtectedRoute><KardexPage /></ProtectedRoute>}
          />
          <Route
            path="/proveedores"
            element={<ProtectedRoute><ProveedoresPage /></ProtectedRoute>}
          />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}
