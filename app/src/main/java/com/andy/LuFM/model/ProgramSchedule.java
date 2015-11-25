package com.andy.LuFM.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy.Wang on 2015/11/25.
 */
public class ProgramSchedule {
    public int dayOfWeek;
    public List<ProgramNode> mLstProgramNodes;

    public boolean addProgramNode(ProgramNode node) {
        if (node == null) {
            return false;
        }
        if (this.mLstProgramNodes == null) {
            this.mLstProgramNodes = new ArrayList();
        }
        node.prevSibling = null;
        node.nextSibling = null;
        if (this.mLstProgramNodes.size() > 0) {
            int index = -1;
            for (int i = 0; i < this.mLstProgramNodes.size(); i++) {
                if (((ProgramNode) this.mLstProgramNodes.get(i)).uniqueId == node.uniqueId) {
                    return false;
                }
                if (((ProgramNode) this.mLstProgramNodes.get(i)).sequence < node.sequence) {
                    index = i;
                }
            }
            if (index != -1) {
                Node prevNode = (Node) this.mLstProgramNodes.get(index);
                node.prevSibling = prevNode;
                node.nextSibling = prevNode.nextSibling;
                if (prevNode.nextSibling != null) {
                    prevNode.nextSibling.prevSibling = node;
                }
                prevNode.nextSibling = node;
                if (index < this.mLstProgramNodes.size() - 1) {
                    this.mLstProgramNodes.add(index + 1, node);
                } else {
                    this.mLstProgramNodes.add(node);
                }
            } else {
                node.nextSibling = (Node) this.mLstProgramNodes.get(0);
                ((ProgramNode) this.mLstProgramNodes.get(0)).prevSibling = node;
                this.mLstProgramNodes.add(0, node);
            }
        } else {
            this.mLstProgramNodes.add(node);
        }
        return true;
    }

}
