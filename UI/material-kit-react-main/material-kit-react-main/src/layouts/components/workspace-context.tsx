import {
    createContext,
    useCallback,
    useMemo,
    useState,
    useEffect,
} from 'react';

export type Workspace = {
    id: string;
    name: string;
    logo: string;
    plan: string;
};

type WorkspaceContextType = {
    workspace: Workspace | null;
    workspaceId: string | null;
    workspaces: Workspace[];
    setWorkspace: (workspace: Workspace) => void;
    setWorkspaces: React.Dispatch<React.SetStateAction<Workspace[]>>;
};

export const WorkspaceContext =
    createContext<WorkspaceContextType | null>(null);

const ACTIVE_WORKSPACE_ID = 'active_workspace_id';
const ACTIVE_WORKSPACE_DATA = 'active_workspace_data';

export function WorkspaceProvider({
    children,
}: {
    children: React.ReactNode;
}) {
    const [workspaces, setWorkspaces] = useState<Workspace[]>([]);

    const [workspaceId, setWorkspaceId] = useState<string | null>(() =>
        localStorage.getItem(ACTIVE_WORKSPACE_ID)
    );

    const [workspace, setWorkspaceState] = useState<Workspace | null>(() => {
        const stored = localStorage.getItem(ACTIVE_WORKSPACE_DATA);

        return stored ? (JSON.parse(stored) as Workspace) : null;
    });

    useEffect(() => {
        if (!workspaceId || workspaces.length === 0) {
            return;
        }

        const latestWorkspace = workspaces.find(
            (item) => item.id === workspaceId
        );

        if (!latestWorkspace) {
            return;
        }

        setWorkspaceState(latestWorkspace);

        localStorage.setItem(
            ACTIVE_WORKSPACE_DATA,
            JSON.stringify(latestWorkspace)
        );
    }, [workspaceId, workspaces]);

    const setWorkspace = useCallback((newWorkspace: Workspace) => {
        setWorkspaceId(newWorkspace.id);
        setWorkspaceState(newWorkspace);

        localStorage.setItem(
            ACTIVE_WORKSPACE_ID,
            newWorkspace.id
        );

        localStorage.setItem(
            ACTIVE_WORKSPACE_DATA,
            JSON.stringify(newWorkspace)
        );
    }, []);

    const value = useMemo(
        () => ({
            workspace,
            workspaceId,
            workspaces,
            setWorkspace,
            setWorkspaces,
        }),
        [
            workspace,
            workspaceId,
            workspaces,
            setWorkspace,
        ]
    );

    return (
        <WorkspaceContext.Provider value={value}>
            {children}
        </WorkspaceContext.Provider>
    );
}